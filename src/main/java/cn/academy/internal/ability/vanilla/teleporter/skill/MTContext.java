package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.ability.vanilla.teleporter.util.TPSkillHelper;
import cn.academy.internal.datapart.CPData;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public class MTContext extends Context<MarkTeleport> {
    private static final String MSG_EXECUTE = "execute";
    private static final String MSG_SOUND = "sound";

    private static final double MINIMUM_VALID_DISTANCE = 3.0;

    private int ticks = 0;
    private final float exp = ctx.getSkillExp();

    public MTContext(EntityPlayer p) {
        super(p, MarkTeleport.INSTANCE);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onKeyUp() {
        sendToServer(MSG_EXECUTE);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onKeyAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_tick() {
        ticks++;
    }

    @Listener(channel = MSG_EXECUTE, side = Side.SERVER)
    private void s_execute() {
        Vec3d dest = getDest(player, ticks);
        float distance = (float) dest.distanceTo(new Vec3d(player.posX, player.posY, player.posZ));
        if (distance < MINIMUM_VALID_DISTANCE) {
            // TODO: Play abort sound
        } else {
            sendToClient(MSG_SOUND);
            float overload = MathUtils.lerpf(40, 20, exp);
            ctx.consumeWithForce(overload, distance * getCPB(exp));
            if (player.isRiding()) {
                player.dismountRidingEntity();
            }
            player.setPositionAndUpdate(dest.x, dest.y, dest.z);
            float expincr = 0.00018f * distance;
            ctx.addSkillExp(expincr);
            player.fallDistance = 0;
            ctx.setCooldown((int) MathUtils.lerpf(30, 0, exp));
            TPSkillHelper.incrTPCount(player);
        }
        terminate();
    }

    private double getMaxDist(float exp, float cp, int ticks) {
        double max = MathUtils.lerpf(25, 60, exp);
        double cplim = cp / getCPB(exp);
        return Math.min((ticks + 1) * 2, Math.min(max, cplim));
    }

    /**
     * @return Consumption per block
     */
    private float getCPB(float exp) {
        return MathUtils.lerpf(12, 4, exp);
    }

    public Vec3d getDest(EntityPlayer player, int ticks) {
        CPData cpData = CPData.get(player);
        double dist = getMaxDist(ctx.getSkillExp(), cpData.getCP(), ticks);
        RayTraceResult mop = Raytrace.traceLiving(player, dist);
        double x;
        double y;
        double z;

        if (mop.typeOfHit != RayTraceResult.Type.MISS) {
            x = mop.hitVec.x;
            y = mop.hitVec.y;
            z = mop.hitVec.z;

            if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                switch (mop.sideHit) {
                    case DOWN:
                        y -= 1.0;
                        break;
                    case UP:
                        y += 1.8;
                        break;
                    case NORTH:
                        z -= 0.6;
                        y = mop.getBlockPos().getY() + 1.7;
                        break;
                    case SOUTH:
                        z += 0.6;
                        y = mop.getBlockPos().getY() + 1.7;
                        break;
                    case WEST:
                        x -= 0.6;
                        y = mop.getBlockPos().getY() + 1.7;
                        break;
                    case EAST:
                        x += 0.6;
                        y = mop.getBlockPos().getY() + 1.7;
                        break;
                }
                // check head
                if (mop.sideHit.getIndex() > 1) {
                    int hx = (int) x;
                    int hy = (int) (y + 1);
                    int hz = (int) z;
                    if (!player.world.isAirBlock(new BlockPos(hx, hy, hz))) {
                        y -= 1.25;
                    }
                }
            } else {
                y += mop.entityHit.getEyeHeight();
            }
        } else {
            Vec3d mo = VecUtils.add(player.getPositionEyes(1f), VecUtils.multiply(player.getLookVec(), dist));
            x = mo.x;
            y = mo.y;
            z = mo.z;
        }
        return new Vec3d(x, y, z);
    }
}
