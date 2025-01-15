package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.lambdalib2.util.IBlockSelector;
import cn.lambdalib2.util.WorldUtils;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class HandlerEntity extends EntityAdvanced {
    private final IBlockSelector blockFilter = (world, x, y, z, block) -> {
        // soon
        return false;
    };

    public final List<MineElem> aliveSims = new ArrayList<>();
    private final int lifeTime;
    public final double range;
    private final EntityPlayer target;
    private final boolean isAdvanced;
    private final List<BlockPos> blockPosBuffer = new ArrayList<>();

    public HandlerEntity(EntityPlayer target, int time, double range, boolean advanced) {
        super(target.world);
        this.lifeTime = time;
        this.range = Math.min(range, 28);
        this.target = target;
        this.isAdvanced = advanced;

        ignoreFrustumCheck = true;
        setPosition(target.posX, target.posY, target.posZ);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public void onFirstUpdate() {
        updateBlocks();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setPosition(target.posX, target.posY, target.posZ);

        if (ticksExisted % 5 == 0) {
            updateBlocks();
        }

        if (ticksExisted > lifeTime) {
            setDead();
        }
    }

    private void updateBlocks() {
        final int LIMIT = 8400; // 20^3 = 64000, this would be fairly abundant
        WorldUtils.getBlocksWithin(blockPosBuffer, this, range, LIMIT, blockFilter);

        aliveSims.clear();
        for (BlockPos bp : blockPosBuffer) {
            int harvestLevel = 0;
            if (isAdvanced) {
                harvestLevel = Math.min(3, world.getBlockState(bp).getBlock().getHarvestLevel(world.getBlockState(bp)) + 1);
            }
            aliveSims.add(new MineElem(bp.getX(), bp.getY(), bp.getZ(), harvestLevel));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        // Implement if needed
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        // Implement if needed
    }

    @Override
    protected void entityInit() {
        // Implement if needed
    }
}
