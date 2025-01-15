package cn.academy.internal.ability.vanilla.generic.client.effect;

import cn.academy.internal.entity.LocalEntity;
import cn.lambdalib2.util.EntityLook;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BloodSprayEffect extends LocalEntity {

    private final EnumFacing dir;
    private final int textureID;
    private final double size;
    private final double rotation;
    private final double[] planeOffset;

    public BloodSprayEffect(World world, BlockPos pos, int side) {
        super(world);
        this.dir = EnumFacing.values()[side];
        this.textureID = RandUtils.rangei(0, 10);
        this.size = RandUtils.ranged(1.1, 1.4) * (side == 0 || side == 1 ? 1.0 : 0.8);
        this.rotation = RandUtils.ranged(0, 360);
        this.planeOffset = new double[]{rand.nextGaussian() * 0.15, rand.nextGaussian() * 0.15};

        this.ignoreFrustumCheck = true;
        this.setSize(1.5f, 2.2f);

        initializePosition(world, pos);
        new EntityLook(dir).applyToEntity(this);
    }

    private void initializePosition(World world, BlockPos pos) {
        double xm, ym, zm;
        double dx, dy, dz;

        dx = world.getBlockState(pos).getBoundingBox(world, pos).maxX - world.getBlockState(pos).getBoundingBox(world, pos).minX;
        dy = world.getBlockState(pos).getBoundingBox(world, pos).maxY - world.getBlockState(pos).getBoundingBox(world, pos).minY;
        dz = world.getBlockState(pos).getBoundingBox(world, pos).maxZ - world.getBlockState(pos).getBoundingBox(world, pos).minZ;

        xm = (world.getBlockState(pos).getBoundingBox(world, pos).maxX + world.getBlockState(pos).getBoundingBox(world, pos).minX) / 2;
        ym = (world.getBlockState(pos).getBoundingBox(world, pos).maxY + world.getBlockState(pos).getBoundingBox(world, pos).minY) / 2;
        zm = (world.getBlockState(pos).getBoundingBox(world, pos).maxZ + world.getBlockState(pos).getBoundingBox(world, pos).minZ) / 2;

        this.setPosition(
                pos.getX() + xm + dir.getXOffset() * 0.51 * dx,
                pos.getY() + ym + dir.getYOffset() * 0.51 * dy,
                pos.getZ() + zm + dir.getZOffset() * 0.51 * dz
        );
    }

    @Override
    public void onUpdate() {
        if (ticksExisted > 1200 || world.getBlockState(getPosition()).getBlock() == Blocks.AIR) {
            setDead();
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    public boolean isWall() {
        return dir == EnumFacing.UP || dir == EnumFacing.DOWN;
    }

    public int getTextureID() {
        return textureID;
    }

    public double getSize() {
        return size;
    }

    public double getRotation() {
        return rotation;
    }

    public double[] getPlaneOffset() {
        return planeOffset;
    }
}
