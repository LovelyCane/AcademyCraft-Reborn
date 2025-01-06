package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.academy.event.BlockDestroyEvent;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import static ic2.core.util.Util.lerp;

@SuppressWarnings("unused")
public abstract class MRContext extends Context<MineRaysBase> {
    public static final String MSG_PARTICLES = "particles";
    private final float exp = ctx.getSkillExp();
    private int x = -1;
    private int y = -1;
    private int z = -1;
    private float hardnessLeft = Float.MAX_VALUE;
    private float overloadKeep = 0f;

    private float range = 0f;
    private float speed_l = 0f;
    private float speed_r = 0f;
    private float cp_l = 0f;
    private float cp_r = 0f;
    private float o_l = 0f;
    private float o_r = 0f;
    private float cd_l = 0f;
    private float cd_r = 0f;
    private float expincr = 0f;
    private int harvestLevel = 0;

    public MRContext(EntityPlayer p, MineRaysBase skill) {
        super(p, skill);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onEnd() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_onStart() {
        ctx.consume(lerp(o_l, o_r, exp), 0);
        overloadKeep = ctx.cpData.getOverload();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if (ctx.cpData.getOverload() < overloadKeep) {
            ctx.cpData.setOverload(overloadKeep);
        }
        if (!ctx.consume(0, lerp(cp_l, cp_r, exp)) && !isRemote()) {
            terminate();
        }

        RayTraceResult result = Raytrace.traceLiving(player, range, EntitySelectors.nothing());
        BlockPos pos = result.getBlockPos();
        if (pos.getX() != x || pos.getY() != y || pos.getZ() != z) {
            Block block = world().getBlockState(pos).getBlock();
            IBlockState state = world().getBlockState(pos);
            if (!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(player, pos)) && block.getHarvestLevel(world().getBlockState(pos)) <= harvestLevel) {
                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
                hardnessLeft = state.getBlockHardness(world(), pos);
                if (hardnessLeft < 0) {
                    hardnessLeft = Float.MAX_VALUE;
                }
            } else {
                x = -1;
                y = -1;
                z = -1;
            }
        } else {
            hardnessLeft -= lerp(speed_l, speed_r, exp);
            if (hardnessLeft <= 0) {
                Block block = world().getBlockState(new BlockPos(x, y, z)).getBlock();
                onBlockBreak(world(), new BlockPos(x, y, z), block);
                ctx.addSkillExp(expincr);
                x = -1;
                y = -1;
                z = -1;
            }
            sendToClient(MSG_PARTICLES, x, y, z);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.SERVER)
    private void s_terminated() {
        ctx.setCooldown((int) lerp(cd_l, cd_r, exp));
    }

    public void setRange(float _range) {
        range = _range;
    }

    public void setHarvestLevel(int _level) {
        harvestLevel = _level;
    }

    public void setSpeed(float l, float r) {
        speed_l = l;
        speed_r = r;
    }

    public void setConsumption(float l, float r) {
        cp_l = l;
        cp_r = r;
    }

    public void setOverload(float l, float r) {
        o_l = l;
        o_r = r;
    }

    public void setCooldown(float l, float r) {
        cd_l = l;
        cd_r = r;
    }

    public void setExpIncr(float amt) {
        expincr = amt;
    }

    protected abstract void onBlockBreak(World world, BlockPos pos, Block block);
}
