package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.entity.EntitySilbarn;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.WorldUtils;
import cn.lambdalib2.util.entityx.event.CollideEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class RBContext extends Context<RayBarrage> {
    public static final String MSG_START = "start";
    public static final String MSG_SYNC_SILBARN = "sync_silbarn";
    public static final String MSG_EXECUTE = "execute";
    public static final String MSG_EFFECT_PRERAY = "effect_preray";
    public static final String MSG_EFFECT_BARRAGE = "effect_barrage";
    private static final double DISPLAY_RAY_DIST = 20;
    private static final double RAY_DIST = 20;

    private boolean hit = false;
    private EntitySilbarn silbarn;

    public RBContext(EntityPlayer player) {
        super(player, RayBarrage.INSTANCE);
    }

    private float getPlainDamage(float exp) {
        return MathUtils.lerpf(25, 60, exp);
    }

    private float getScatteredDamage(float exp) {
        return MathUtils.lerpf(10, 18, exp);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_onKeyDown() {
        sendToServer(RBContext.MSG_START);
    }

    @Listener(channel = RBContext.MSG_SYNC_SILBARN, side = Side.CLIENT)
    private void c_sync_silbarn(EntitySilbarn silbarn) {
        this.silbarn = silbarn;
    }

    @Listener(channel = RBContext.MSG_START, side = Side.SERVER)
    private void s_consume() {
        float exp = ctx.getSkillExp();

        RayTraceResult pos = Raytrace.traceLiving(player, DISPLAY_RAY_DIST);
        if (pos.entityHit instanceof EntitySilbarn && !((EntitySilbarn) pos.entityHit).isHit()) {
            hit = true;
            silbarn = (EntitySilbarn) pos.entityHit;
        }

        sendToClient(RBContext.MSG_SYNC_SILBARN, silbarn);

        float cp = MathUtils.lerpf(450, 380, exp);
        float overload = MathUtils.lerpf(300, 140, exp);
        if (!ctx.consume(overload, cp)) terminate();
        sendToSelf(RBContext.MSG_EXECUTE);
    }

    @Listener(channel = RBContext.MSG_EXECUTE, side = Side.SERVER)
    private void s_execute() {
        float exp = ctx.getSkillExp();

        double tx;
        double ty;
        double tz;
        if (hit) {
            if (silbarn == null) return;
            tx = silbarn.posX;
            ty = silbarn.posY;
            tz = silbarn.posZ;
            silbarn.postEvent(new CollideEvent(new RayTraceResult(silbarn)));
            sendToClient(RBContext.MSG_EFFECT_BARRAGE, silbarn);
            float range = 55;

            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;

            float minYaw = yaw - range / 2;
            float maxYaw = yaw + range / 2;

            float minPitch = pitch - range;
            float maxPitch = pitch + range;

            Predicate<Entity> selector = EntitySelectors.exclude(silbarn, player);

            Vec3d pos = player.getPositionVector();
            Vec3d mo = player.getLookVec();

            Vec3d v0 = player.getPositionVector();
            Vec3d v1 = v0.add(mo.rotateYaw(minYaw).rotatePitch(minPitch).scale(RAY_DIST));
            Vec3d v2 = v0.add(mo.rotateYaw(minYaw).rotatePitch(maxPitch).scale(RAY_DIST));
            Vec3d v3 = v0.add(mo.rotateYaw(maxYaw).rotatePitch(maxPitch).scale(RAY_DIST));
            Vec3d v4 = v0.add(mo.rotateYaw(maxYaw).rotatePitch(minPitch).scale(RAY_DIST));

            AxisAlignedBB aabb = WorldUtils.minimumBounds(v0, v1, v2, v3, v4);
            List<Entity> list = WorldUtils.getEntities(player.world, aabb, selector);

            for (Entity e : list) {
                double dx = e.posX - player.posX;
                double dy = (e.posY + e.getEyeHeight()) - (player.posY + player.getEyeHeight());
                double dz = e.posZ - player.posZ;

                float eyaw = (float) -(Math.atan2(dx, dz) * 180.0D / Math.PI);
                float epitch = (float) -(Math.atan2(dy, Math.sqrt(dz * dz + dz * dz)) * 180.0D / Math.PI);
                if (MathUtils.angleYawinRange(minYaw, maxYaw, eyaw) && (minPitch <= epitch && epitch <= maxPitch)) {
                    MDDamageHelper.attack(ctx, e, getScatteredDamage(exp));
                }
            }
        } else {
            org.apache.commons.lang3.tuple.Pair<Vec3d, RayTraceResult> pres = Raytrace.getLookingPos(player, RAY_DIST);
            Vec3d pos = pres.getLeft();
            RayTraceResult result = pres.getRight();

            tx = pos.x;
            ty = pos.y;
            tz = pos.z;

            if (result != null && result.entityHit != null)
                MDDamageHelper.attack(ctx, result.entityHit, getPlainDamage(exp));
        }

        sendToClient(RBContext.MSG_EFFECT_PRERAY, player.posX, player.posY, player.posZ, tx, ty, tz, hit);

        ctx.setCooldown((int) MathUtils.lerpf(100, 40, exp));
        ctx.addSkillExp(0.005f);
        terminate();
    }
}
