package cn.academy.internal.ability.vanilla.electromaster.skill;


import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ThunderBoltContext extends Context<ThunderBolt> {
    public static final String MSG_PERFORM = "perform";
    private final float exp = ctx.getSkillExp();
    private final float aoeDamage = MathUtils.lerpf(6, 15, exp);
    private final float damage = MathUtils.lerpf(10, 25, exp);

    public ThunderBoltContext(EntityPlayer p) {
        super(p, ThunderBolt.INSTANCE);
    }

    private float getExpIncr(boolean effective) {
        return effective ? 0.005f : 0.003f;
    }

    private boolean consume() {
        float overload = MathUtils.lerpf(50, 27, exp);
        int cp = (int) MathUtils.lerp(280, 420, exp);
        return ctx.consume(overload, cp);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_onKeyDown() {
        sendToServer(MSG_PERFORM);
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    private void s_perform() {
        if (consume()) {
            AttackData ad = getAttackData();

            sendToClient(MSG_PERFORM, ad);

            boolean effective = false;

            if (ad.target != null) {
                effective = true;
                EMDamageHelper.attack(ctx, ad.target, damage);
                if (exp > 0.2 && RandUtils.ranged(0, 1) < 0.8 && ad.target instanceof EntityLivingBase) {
                    ((EntityLivingBase) ad.target).addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("slowness")), 40, 3));
                }
            }

            for (Entity e : ad.aoes) {
                effective = true;
                EMDamageHelper.attack(ctx, e, aoeDamage);

                if (exp > 0.2 && RandUtils.ranged(0, 1) < 0.8 && e instanceof EntityLivingBase) {
                    ((EntityLivingBase) e).addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionFromResourceLocation("slowness")), 20, 3));
                }
            }

            ctx.addSkillExp(getExpIncr(effective));
            ctx.setCooldown((int) MathUtils.lerpf(120, 50, exp));
        }
        terminate();
    }

    public AttackData getAttackData() {
        RayTraceResult result = Raytrace.traceLiving(player, ThunderBolt.RANGE);
        Vec3d end;
        end = result.hitVec;
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            end = end.add(0, result.entityHit.getEyeHeight(), 0);
        }

        boolean hitEntity = result.entityHit != null;
        Predicate<Entity> exclusion = hitEntity ? EntitySelectors.exclude(player, result.entityHit) : EntitySelectors.exclude(player);
        Entity target = hitEntity ? result.entityHit : null;
        List<Entity> aoes = WorldUtils.getEntities(player.getEntityWorld(), end.x, end.y, end.z, ThunderBolt.AOE_RANGE, EntitySelectors.living().and(exclusion));

        AttackData ad = new AttackData();
        ad.aoes = aoes;
        ad.target = target;
        ad.point = end;
        return ad;
    }
}
