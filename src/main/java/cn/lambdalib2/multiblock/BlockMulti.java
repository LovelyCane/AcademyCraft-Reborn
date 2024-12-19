package cn.lambdalib2.multiblock;

import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author WeathFolD
 */

public abstract class BlockMulti extends BlockContainer {
    private final List<SubBlockPos> subList = new ArrayList<>();
    List<SubBlockPos>[] buffer;

    private final AxisAlignedBB[] renderBB = new AxisAlignedBB[8];

    @SideOnly(Side.CLIENT)
    double[][] rotCenters;

    boolean init = false;

    public static class SubBlockPos {
        public final int dx, dy, dz;

        public SubBlockPos(int _dx, int _dy, int _dz) {
            dx = _dx;
            dy = _dy;
            dz = _dz;
        }
    }

    /**
     * notice that you must call finishInit() in your own subclass ctor.
     */
    public BlockMulti(Material p_i45386_1_) {
        super(p_i45386_1_);
        addSubBlock(0, 0, 0);
    }

    public void addSubBlock(int dx, int dy, int dz) {
        if (init) {
            throw new RuntimeException("Trying to add a sub block after block init finished");
        }
        subList.add(new SubBlockPos(dx, dy, dz));
    }

    /**
     * Accept an int[][3] array, add all the array element as a single subPos
     * inside the list.
     */
    public void addSubBlock(int[][] data) {
        for (int[] s : data) {
            addSubBlock(s[0], s[1], s[2]);
        }
    }

    /**
     * Get the render bounding box of this BlockMulti at the given block
     * position (as origin block) Usually used on TileEntity rendering.
     */
    public AxisAlignedBB getRenderBB(BlockPos pos, EnumFacing dir) {
        // Lazy init
        if (renderBB[dir.ordinal()] == null) {
            Vec3d[] vecs = new Vec3d[subList.size() * 2];
            for (int i = 0; i < subList.size(); ++i) {
                SubBlockPos rot = rotate(subList.get(i), dir);
                vecs[i * 2] = new Vec3d(rot.dx, rot.dy, rot.dz);
                vecs[i * 2 + 1] = new Vec3d(rot.dx + 1, rot.dy + 1, rot.dz + 1);
            }

            renderBB[dir.ordinal()] = WorldUtils.minimumBounds(vecs);
        }

        AxisAlignedBB box = renderBB[dir.ordinal()];
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(box.minX + x, box.minY + y, box.minZ + z, box.maxX + x, box.maxY + y, box.maxZ + z);
    }

    /**
     * You MUST call this via your ctor, after init all the blocks.
     */
    @SuppressWarnings("sideonly")
    public void finishInit() {
        // Pre-init rotated position offset list.
        buffer = new ArrayList[6];

        for (int i = 2; i <= 5; ++i) {
            EnumFacing dir = EnumFacing.values()[i];
            buffer[i] = new ArrayList<>();
            for (SubBlockPos s : subList) {
                buffer[i].add(rotate(s, dir));
            }
        }

        if (FMLCommonHandler.instance().getSide().isClient()) {
            double[] arr = getRotCenter();
            rotCenters = new double[][]{{}, {}, {arr[0], arr[1], arr[2]}, {-arr[0], arr[1], -arr[2]}, {arr[2], arr[1], -arr[0]}, {-arr[2], arr[1], arr[0]}};
        }

        // Finished, set the flag and encapsulate the instance.
        init = true;
    }

    // Rotation API
    // Some lookup tables
    private static final EnumFacing[] rotMap = {NORTH, EAST, SOUTH, WEST};

    private static final double[] drMap = {0, 0, 180, 0, -90, 90};
    private static final double[][] offsetMap = {{0, 0}, // placeholder
            {0, 0}, // placeholder
            {0, 0}, {1, 1}, {0, 1}, {1, 0}};

    public double[] getPivotOffset(InfoBlockMulti info) {
        return getPivotOffset(info.dir);
    }

    public EnumFacing getRotation(int l) {
        return rotMap[l];
    }

    public double[] getPivotOffset(EnumFacing dir) {
        return offsetMap[dir.ordinal()];
    }

    @SideOnly(Side.CLIENT)
    public abstract double[] getRotCenter();

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        EnumFacing dir = rotMap[l];
        updateDirInfo(world, pos, dir);
    }

    private void updateDirInfo(World world, BlockPos pos, EnumFacing dir) {
        // Set the origin block.
        TileEntity te = world.getTileEntity(pos);
        trySetBlockInfo(te, new InfoBlockMulti(te, dir, 0));

        List<SubBlockPos> rotatedList = buffer[dir.ordinal()];
        // Check done in ItemBlockMulti, brutely replace here.
        for (int i = 1; i < rotatedList.size(); ++i) {
            SubBlockPos sub = rotatedList.get(i);
            BlockPos npos = pos.add(sub.dx, sub.dy, sub.dz);
            world.setBlockState(npos, blockState.getBaseState());
            te = world.getTileEntity(npos);

            trySetBlockInfo(te, new InfoBlockMulti(te, dir, i));
        }
    }

    private void trySetBlockInfo(TileEntity te, InfoBlockMulti info) {
        if (te instanceof IMultiTile) {
            info.setLoaded();
            ((IMultiTile) te).setBlockInfo(info);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IMultiTile)) {
            Debug.error("Didn't find correct tile when breaking a BlockMulti.");
            return;
        }
        InfoBlockMulti info = ((IMultiTile) te).getBlockInfo();
        BlockPos origin = getOrigin(te);
        if (origin == null) return;

        List<SubBlockPos> rotatedList = buffer[info.dir.ordinal()];
        for (SubBlockPos sbp : rotatedList) {
            world.setBlockToAir(origin.add(sbp.dx, sbp.dy, sbp.dz));
        }
    }

    public BlockPos getOrigin(World world, BlockPos pos) {
        return getOrigin(world.getTileEntity(pos));
    }

    public BlockPos getOrigin(TileEntity te) {
        TileEntity ret = getOriginTile(te);
        return ret == null ? null : ret.getPos();
    }

    public TileEntity getOriginTile(World world, BlockPos pos) {
        TileEntity now = world.getTileEntity(pos);
        return getOriginTile(now);
    }

    public TileEntity getOriginTile(TileEntity now) {
        if (!(now instanceof IMultiTile)) {
            return null;
        }
        InfoBlockMulti info = ((IMultiTile) now).getBlockInfo();
        if (info == null || !info.isLoaded()) return null;
        SubBlockPos sbp = buffer[info.dir.ordinal()].get(info.subID);
        return validate(now.getWorld().getTileEntity(now.getPos().add(-sbp.dx, -sbp.dy, -sbp.dz)));
    }

    // Internal
    public static SubBlockPos rotate(SubBlockPos s, EnumFacing dir) {
        switch (dir) {
            case EAST:
                return new SubBlockPos(-s.dz, s.dy, s.dx);
            case WEST:
                return new SubBlockPos(s.dz, s.dy, -s.dx);
            case SOUTH:
                return new SubBlockPos(-s.dx, s.dy, -s.dz);
            case NORTH:
                return new SubBlockPos(s.dx, s.dy, s.dz);
            default:
                throw new RuntimeException("Invalid rotate direction");
        }
    }

    double getRotation(InfoBlockMulti info) {
        return drMap[info.dir.ordinal()];
    }

    private TileEntity validate(TileEntity te) {
        return te instanceof IMultiTile ? te : null;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}