package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.entity.EntityMdBall;
import cn.academy.internal.entity.EntityMdRaySmall;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElectronBomb extends Skill {
    public static final ElectronBomb Instance = new ElectronBomb();

    private ElectronBomb() {
        super("electron_bomb", 1);
    }

    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, Ctx::new);
    }

    static final String MsgEffect = "effect";
    static final int Life = 20, LifeImproved = 5;
    static final double Distance = 15;

    static Vec3d getDest(EntityPlayer player) {
        return Raytrace.getLookingPos(player, Distance).getLeft();
    }

    public static class Ctx extends Context<ElectronBomb> {

        public Ctx(EntityPlayer _player) {
            super(_player, Instance);
        }

        @Listener(channel = MSG_MADEALIVE, side = Side.SERVER)
        private void s_Execute() {
            float exp = ctx.getSkillExp();
            EntityMdBall ball = new EntityMdBall(
                player,
                ctx.getSkillExp() > 0.8f ? LifeImproved : Life,
                target -> {
                    RayTraceResult trace = Raytrace.perform(player.world, new Vec3d(target.posX, target.posY + player.eyeHeight, target.posZ),
                        getDest(player), EntitySelectors.exclude(player).and(EntitySelectors.of(EntityMdBall.class).negate()));
                    if (trace != null && trace.entityHit != null)
                        MDDamageHelper.attack(ctx, trace.entityHit, getDamage(exp));

                    NetworkMessage.sendToAllAround(
                        TargetPoints.convert(player, 20),
                        EffectDelegate.INSTANCE,
                        MsgEffect,
                        target
                    );
                });
            player.world.spawnEntity(ball);

            ctx.addSkillExp(.005f);
            ctx.setCooldown((int) MathUtils.lerpf(20, 10, exp));
            terminate();
        }

        private float getDamage(float exp) {
            return MathUtils.lerpf(6, 12, exp);
        }

    }

    public static class EffectDelegate {
        public static final EffectDelegate INSTANCE = new EffectDelegate();

        EffectDelegate() {}

        public static void preInit() {
            NetworkS11n.addDirectInstance(INSTANCE);
        }

        @SideOnly(Side.CLIENT)
        @Listener(channel = MsgEffect, side = Side.CLIENT)
        private void onSpawnEffect(EntityMdBall ball) {
            EntityPlayer player = ball.getSpawner();
            if (player == null)
                return;
            Vec3d dest = Raytrace.getLookingPos(player, Distance).getLeft();
            EntityMdRaySmall ray = new EntityMdRaySmall(ball.getEntityWorld());
            ray.setFromTo(ball.posX, ball.posY + player.eyeHeight, ball.posZ, dest.x, dest.y, dest.z);
            ray.viewOptimize = false;
            player.world.spawnEntity(ray);
        }
    }
}
