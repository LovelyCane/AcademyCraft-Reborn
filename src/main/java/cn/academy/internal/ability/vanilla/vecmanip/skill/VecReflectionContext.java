package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.internal.ability.context.Context;
import cn.academy.internal.event.ability.ReflectEvent;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflection.MSG_EFFECT;
import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflection.MSG_REFLECT_ENTITY;
import static cn.lambdalib2.util.MathUtils.lerpf;
import static cn.lambdalib2.util.VecUtils.*;

public class VecReflectionContext extends Context<VecReflection> {
    private final Set<Entity> visited = new HashSet<>();

    public VecReflectionContext(EntityPlayer player) {
        super(player, VecReflection.INSTANCE);
    }

    @NetworkMessage.Listener(channel = MSG_MADEALIVE, side = Side.SERVER)
    public void s_makeAlive() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @NetworkMessage.Listener(channel = MSG_TERMINATED, side = {Side.SERVER, Side.CLIENT})
    public void g_terminate() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @NetworkMessage.Listener(channel = MSG_TICK, side = Side.SERVER)
    public void s_tick() {
        int range = 4;
        List<Entity> entities = WorldUtils.getEntities(player, range, t -> true);
        entities.removeAll(visited);

        entities.stream().filter(t -> !EntityAffection.isMarked(t)).forEach(entity -> {
            EntityAffection.AffectInfo affectInfo = EntityAffection.getAffectInfo(entity);
            if (affectInfo instanceof EntityAffection.Affected) {
                float difficulty = ((EntityAffection.Affected) affectInfo).getDifficulty();
                if (entity instanceof EntityFireball) {
                    if (consumeEntity(difficulty)) {
                        createNewFireball((EntityFireball) entity);
                        ctx.addSkillExp(difficulty * 0.0008f);
                        sendToClient(MSG_REFLECT_ENTITY, entity);
                    }
                } else {
                    if (consumeEntity(difficulty)) {
                        reflect(entity, player);
                        EntityAffection.mark(entity);
                        ctx.addSkillExp(difficulty * 0.0008f);
                        sendToClient(MSG_REFLECT_ENTITY, entity);
                    }
                }
            }
        });

        visited.addAll(entities);

        if (!consumeNormal()) {
            terminate();
        }
    }

    public static void reflect(Entity entity, EntityPlayer player) {
        Vec3d currentVelocity = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);

        // 计算投掷物的反向速度，实现原路返回
        Vec3d reversedVelocity = currentVelocity.scale(-1);

        // 设置新速度，使其按原路返回
        setMotion(entity, reversedVelocity);
    }

    private void createNewFireball(EntityFireball source) {
        source.setDead();

        EntityLivingBase shootingEntity = source.shootingEntity;
        EntityFireball fireball;

        if (source instanceof EntityLargeFireball) {
            fireball = new EntityLargeFireball(world(), shootingEntity, shootingEntity.posX, shootingEntity.posY, shootingEntity.posZ);
            ((EntityLargeFireball) fireball).explosionPower = ((EntityLargeFireball) source).explosionPower;
        } else {
            fireball = new EntitySmallFireball(world(), source.posX, source.posY, source.posZ, source.posX, source.posY, source.posZ);
            if (source.shootingEntity != null) {
                fireball = new EntitySmallFireball(world(), shootingEntity, shootingEntity.posX, shootingEntity.posY, shootingEntity.posZ);
            }
        }

        fireball.setPosition(source.posX, source.posY, source.posZ);
        Vec3d lookPos = Raytrace.getLookingPos(player, 20).getLeft();
        double speed = new Vec3d(source.motionX, source.motionY, source.motionZ).length();
        Vec3d vel = multiply(subtract(lookPos, entityHeadPos(source)).normalize(), speed);
        setMotion(fireball, vel);
        EntityAffection.mark(fireball);
        world().spawnEntity(fireball);
    }

    @NetworkMessage.Listener(channel = MSG_TICK, side = Side.CLIENT)
    public void c_tick() {
        if (!consumeNormal()) {
            terminate();
        }
    }

    @SubscribeEvent
    public void onReflect(ReflectEvent evt) {
        if (evt.target.equals(player)) {
            evt.setCanceled(true);
            Vec3d dpos = subtract(entityHeadPos(evt.player), entityHeadPos(player));
            sendToClient(MSG_EFFECT, add(add(player.getPositionVector(), new Vec3d(0, RandUtils.ranged(0.4, 1.3), 0)), multiply(dpos.normalize(), 0.5)));
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent evt) {
        if (evt.getEntityLiving().equals(player)) {
            handleAttack(evt.getSource(), evt.getAmount(), false);
            evt.setCanceled(true);
            Entity source = evt.getSource().getTrueSource();
            if (source != null) {
                source.attackEntityFrom(evt.getSource(), evt.getAmount());
                Vec3d pos = evt.getSource().getDamageLocation();
            }
        }
    }


    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {
        if (evt.getEntityLiving().equals(player) && evt.getAmount() <= 9999) {
            Pair<Boolean, Float> result = handleAttack(evt.getSource(), evt.getAmount(), false);
            float dmg = result.getRight();
            evt.setAmount(dmg);
            DamageSource damageSource = evt.getSource();

            Entity source = damageSource.getTrueSource();
            if (source != null) {
                source.attackEntityFrom(damageSource, evt.getAmount());
            }
        }
    }

    /**
     * @param passby If passby=true, and this isn't a complete absorb, the action will not perform. Else it will.
     * @return (Whether action had been really performed, processed damage)
     */
    private Pair<Boolean, Float> handleAttack(DamageSource dmgSource, float dmg, boolean passby) {
        float reflectDamage = lerpf(0.6f, 1.2f, ctx.getSkillExp()) * dmg;

        float consumeRatio = consumeDamage(dmg);
        float offsetDamage = dmg * (1 - consumeRatio);

        if (!passby) {
            return Pair.of(true, offsetDamage - reflectDamage);
        } else {
            return Pair.of(reflectDamage >= 1, offsetDamage - reflectDamage);
        }
    }

    private boolean consumeEntity(float difficulty) {
        return ctx.consume(0, difficulty * lerpf(300, 160, ctx.getSkillExp()));
    }

    private float consumeDamage(float damage) {
        float cpAvailable = ctx.cpData.getCP();
        float cpRequired = lerpf(20, 15, ctx.getSkillExp()) * damage;

        if (cpAvailable >= cpRequired) {
            ctx.consumeWithForce(0, cpRequired);
            return 1.0f;
        } else {
            float ratio = cpAvailable / cpRequired;
            ctx.consumeWithForce(0, cpAvailable);
            return ratio;
        }
    }

    private boolean consumeNormal() {
        return ctx.consume(0, lerpf(10, 5, ctx.getSkillExp()));
    }
}