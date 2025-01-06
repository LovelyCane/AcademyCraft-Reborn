package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.academy.ability.context.DelegateState;
import cn.academy.ability.context.IStateProvider;
import cn.academy.internel.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import static cn.lambdalib2.util.MathUtils.lerpf;

@SuppressWarnings("unused")
public class PlasmaCannonContext extends Context<PlasmaCannon> implements IStateProvider {
    public static final String MSG_PERFORM = "perform";
    public static final String MSG_STATECHG = "state_change";
    public static final String MSG_SYNCPOS = "sync_pos";

    public static final int STATE_CHARGING = 0;
    public static final int STATE_GO = 1;

    public static final int MOVING_SPEED = 1;

    private int localTicker = 0;
    private int syncTicker = 0;

    public int state = STATE_CHARGING;

    public Vec3d chargePosition = player.getPositionVector().add(new Vec3d(0.0, 15.0, 0.0));
    private Vec3d destination = null;

    private float overloadKeep = 0f;

    private final int chargeTime = (int) lerpf(60, 30, ctx.getSkillExp());
    private final float overloadToKeep = lerpf(500, 400, ctx.getSkillExp());

    public PlasmaCannonContext(EntityPlayer player) {
        super(player, PlasmaCannon.INSTANCE);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    public void l_keyUp() {
        if (localTicker >= chargeTime) {
            sendToServer(MSG_PERFORM);
        } else {
            terminate();
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    public void l_keyAbort() {
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    public void l_tick() {
        if (isLocal()) {
            localTicker++;

            if (state == STATE_CHARGING && localTicker < chargeTime) {
                tryConsume();
            }

            if (state == STATE_CHARGING && localTicker == chargeTime) {
                ACSounds.playClient(player, "vecmanip.plasma_cannon_t", SoundCategory.AMBIENT, 0.5f);
            }
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    public void s_madeAlive() {
        ctx.consume(overloadToKeep, 0);
        overloadKeep = ctx.cpData.getOverload();
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    public void s_perform() {
        ctx.addSkillExp(0.008f);

        destination = Raytrace.getLookingPos(player, 100, EntitySelectors.living()).getLeft();
        state = STATE_GO;
        localTicker = 0;
        ctx.setCooldown((int) lerpf(1000, 600, ctx.getSkillExp()));
        sendToClient(MSG_STATECHG, destination);
    }

    @Listener(channel = MSG_STATECHG, side = Side.CLIENT)
    public void c_stateChange(Vec3d dest) {
        state = STATE_GO;
        destination = dest;
    }

    @Listener(channel = MSG_SYNCPOS, side = Side.CLIENT)
    public void c_syncPos(Vec3d pos) {
        chargePosition = pos;
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    public void s_tick() {
        if (ctx.cpData.getOverload() < overloadKeep) {
            ctx.cpData.setOverload(overloadKeep);
        }

        localTicker++;

        if (state == STATE_CHARGING) {
            if (localTicker < chargeTime) {
                if (!tryConsume()) {
                    terminate();
                }
            }
        } else if (state == STATE_GO) {
            Vec3d lastPos;
            lastPos = chargePosition;

            tryMove();

            if (Raytrace.perform(world(), lastPos, chargePosition).typeOfHit != RayTraceResult.Type.MISS) {
                explode();
            }

            if (localTicker >= 240 || chargePosition.distanceTo(destination) < 1.5) {
                explode();
            }

            if (syncTicker == 0) {
                syncTicker = 5;
                sendToClient(MSG_SYNCPOS, chargePosition);
            } else {
                syncTicker--;
            }
        }
    }

    private void explode() {
        List<?> entities = WorldUtils.getEntities(world(),
                destination.x, destination.y, destination.z,
                10, EntitySelectors.everything());

        for (Object entity : entities) {
            ctx.attack((Entity) entity, lerpf(80, 150, ctx.getSkillExp()));
            ((Entity) entity).hurtResistantTime = -1;
        }

        Explosion explosion = new Explosion(world(), player,
                destination.x, destination.y, destination.z,
                lerpf(12.0f, 15.0f, ctx.getSkillExp()), false, true);

        if (ctx.canBreakBlock(world())) {
            explosion.doExplosionA();
        }
        explosion.doExplosionB(true);

        terminate();
    }

    public boolean tryConsume() {
        float cp = lerpf(18, 25, ctx.getSkillExp());
        ctx.consume(0, cp);
        return true;
    }

    @Override
    public DelegateState getState() {
        if (state == STATE_CHARGING) {
            if (localTicker < chargeTime) {
                return DelegateState.CHARGE;
            } else {
                return DelegateState.ACTIVE;
            }
        } else {
            return DelegateState.ACTIVE;
        }
    }

    public void tryMove() {
        Vec3d rawDelta = destination.subtract(chargePosition);
        if (rawDelta.length() < 1) return;

        Vec3d delta = rawDelta.normalize().scale(MOVING_SPEED);
        chargePosition = chargePosition.add(delta);
    }
}
