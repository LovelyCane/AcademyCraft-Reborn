package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.WaveEffect;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.WaveEffectUI;
import cn.academy.internal.event.ability.ReflectEvent;
import cn.academy.internal.sound.ACSounds;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflection.MSG_EFFECT;
import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflection.MSG_REFLECT_ENTITY;
import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflectionContext.reflect;
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
        Vec3d lookPos = Raytrace.getLookingPos(player, 20).getLeft();
        double speed = new Vec3d(entity.motionX, entity.motionY, entity.motionZ).length();
        Vec3d vel = multiply(subtract(lookPos, entityHeadPos(entity)).normalize(), speed);
        setMotion(entity, vel);
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
            Entity trueSource = evt.getSource().getTrueSource();
            trueSource.attackEntityFrom(evt.getSource(), evt.getAmount());
        }
    }


    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {
        if (evt.getEntityLiving().equals(player) && evt.getAmount() <= 9999) {
            Pair<Boolean, Float> result = handleAttack(evt.getSource(), evt.getAmount(), false);
            float dmg = result.getRight();
            evt.setAmount(dmg);
            DamageSource damageSource = evt.getSource();

            Entity entity = damageSource.getTrueSource();
            entity.attackEntityFrom(damageSource, evt.getAmount());
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

@SideOnly(Side.CLIENT)
@RegClientContext(VecReflectionContext.class)
class VecReflectionContextC extends ClientContext {
    private ClientRuntime.IActivateHandler activateHandler;
    VecReflectionContext par;
    private final WaveEffectUI ui;

    public VecReflectionContextC(VecReflectionContext par) {
        super(par);
        this.par = par;
        this.ui = new WaveEffectUI(0.4f, 110, 1.6f);
    }

    @NetworkMessage.Listener(channel = MSG_MADEALIVE, side = Side.CLIENT)
    private void l_alive() {
        if (isLocal()) {
            activateHandler = ClientRuntime.ActivateHandlers.terminatesContext(par);
            ClientRuntime.instance().addActivateHandler(activateHandler);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @NetworkMessage.Listener(channel = MSG_TERMINATED, side = Side.CLIENT)
    private void l_terminate() {
        if (isLocal()) {
            ClientRuntime.instance().removeActiveHandler(activateHandler);
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @NetworkMessage.Listener(channel = MSG_REFLECT_ENTITY, side = Side.CLIENT)
    private void c_reflectEntity(Entity entity) {
        reflect(entity, player);
        reflectEffect(entityHeadPos(entity));
    }

    private void reflectEffect(Vec3d point) {
        WaveEffect eff = new WaveEffect(world(), 2, 1.1);
        eff.setPosition(point.x, point.y, point.z);
        eff.rotationYaw = player.rotationYawHead;
        eff.rotationPitch = player.rotationPitch;
        world().spawnEntity(eff);
        playSound(point);
    }

    private void playSound(Vec3d pos) {
        ACSounds.playClient(world(), pos.x, pos.y, pos.z, "vecmanip.vec_reflection", SoundCategory.AMBIENT, 0.5f, 1.0f);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent evt) {
        if (evt.getType() == ElementType.CROSSHAIRS) {
            ui.onFrame(evt.getResolution().getScaledWidth(), evt.getResolution().getScaledHeight());
        }
    }
}