package cn.academy;

import cn.academy.internal.ability.vanilla.electromaster.skill.HandlerEntity;
import cn.academy.internal.client.renderer.entity.RenderHandlerEntity;
import cn.academy.internal.ability.vanilla.generic.client.effect.BloodSprayEffect;
import cn.academy.internal.client.renderer.entity.RenderBloodSprayEffect;
import cn.academy.internal.ability.vanilla.generic.client.effect.SmokeEffect;
import cn.academy.internal.client.renderer.entity.RenderSmokeEffect;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.*;
import cn.academy.internal.ability.vanilla.vecmanip.skill.Tornado;
import cn.academy.internal.client.renderer.entity.RenderTornado;
import cn.academy.internal.client.renderer.entity.*;
import cn.academy.internal.entity.*;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.particle.RenderParticle;
import cn.lambdalib2.renderhook.EntityDummy;
import cn.academy.internal.client.renderer.entity.RenderEntityDummy;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class AcademyCraftEntityRenderList {
    public static final Map<Class<? extends Render>, Class<? extends Entity>> ENTITY_RENDER_MAP = new HashMap<>();

    static {
        ENTITY_RENDER_MAP.put(RenderEntityMagHook.class, EntityMagHook.class);
        ENTITY_RENDER_MAP.put(RenderHandlerEntity.class, HandlerEntity.class);
        ENTITY_RENDER_MAP.put(RenderBloodSprayEffect.class, BloodSprayEffect.class);
        ENTITY_RENDER_MAP.put(RenderSmokeEffect.class, SmokeEffect.class);
        ENTITY_RENDER_MAP.put(RenderParabolaEffect.class, ParabolaEffect.class);
        ENTITY_RENDER_MAP.put(RenderPlasmaBodyEffect.class, PlasmaBodyEffect.class);
        ENTITY_RENDER_MAP.put(RenderStormWingEffect.class, StormWingEffect.class);
        ENTITY_RENDER_MAP.put(RenderWaveEffect.class, WaveEffect.class);
        ENTITY_RENDER_MAP.put(RenderTornado.class, Tornado.class);
        ENTITY_RENDER_MAP.put(RenderEntityArc.class, EntityArc.class);
        ENTITY_RENDER_MAP.put(RenderEntityCoinThrowing.class, EntityCoinThrowing.class);
        ENTITY_RENDER_MAP.put(RenderEntityDiamondShield.class, EntityDiamondShield.class);
        ENTITY_RENDER_MAP.put(RenderEntityMDRay.class, EntityMDRay.class);
        ENTITY_RENDER_MAP.put(RenderEntityTPMarking.class, EntityTPMarking.class);
        ENTITY_RENDER_MAP.put(RenderEntityMarker.class, EntityMarker.class);
        ENTITY_RENDER_MAP.put(RenderEntityMdShield.class, EntityMdShield.class);
        ENTITY_RENDER_MAP.put(RenderEntityRailgunFX.class, EntityRailgunFX.class);
        ENTITY_RENDER_MAP.put(RenderEntityRippleMark.class, EntityRippleMark.class);
        ENTITY_RENDER_MAP.put(RenderEntityBarrageRayPre.class, EntityBarrageRayPre.class);
        ENTITY_RENDER_MAP.put(RenderEntityBloodSplash.class, EntityBloodSplash.class);
        ENTITY_RENDER_MAP.put(RenderEntityMdBall.class, EntityMdBall.class);
        ENTITY_RENDER_MAP.put(RenderEntityMdRayBarrage.class, EntityMdRayBarrage.class);
        ENTITY_RENDER_MAP.put(RenderEntityMdRaySmall.class, EntityMdRaySmall.class);
        ENTITY_RENDER_MAP.put(RenderEntityMineRayBasic.class, EntityMineRayBasic.class);
        ENTITY_RENDER_MAP.put(RenderEntityMineRayExpert.class, EntityMineRayExpert.class);
        ENTITY_RENDER_MAP.put(RenderEntityMineRayLuck.class, EntityMineRayLuck.class);
        ENTITY_RENDER_MAP.put(RenderEntitySilbarn.class, EntitySilbarn.class);
        ENTITY_RENDER_MAP.put(RenderEntitySurroundArc.class, EntitySurroundArc.class);
        ENTITY_RENDER_MAP.put(RenderEntityDummy.class, EntityDummy.class);
        ENTITY_RENDER_MAP.put(RenderParticle.class, Particle.class);
    }

    private AcademyCraftEntityRenderList() {
    }
}
