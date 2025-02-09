package cn.academy.internal.event;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftRegister;
import cn.academy.internal.ability.AbilityPipeline;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.ctrl.ClientHandler;
import cn.academy.internal.ability.vanilla.meltdowner.skill.MDDamageHelper;
import cn.academy.internal.ability.vanilla.teleporter.client.CriticalHitEffect;
import cn.academy.internal.client.CameraPosition;
import cn.academy.internal.client.misc.MusicSystem;
import cn.academy.internal.client.renderer.util.VanillaHandRenderer;
import cn.academy.internal.datapart.CPData;
import cn.academy.internal.datapart.CooldownData;
import cn.academy.internal.datapart.PresetData;
import cn.academy.internal.energy.impl.WirelessSystem;
import cn.academy.internal.util.ACKeyManager;
import cn.lambdalib2.auxgui.AuxGuiHandler;
import cn.lambdalib2.particle.ParticleFactoryBase;
import cn.lambdalib2.util.ControlOverrider;
import cn.lambdalib2.util.GameTimer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AcademyCraftEventManager {
    public static void registerEventBus() {
        MinecraftForge.EVENT_BUS.register(AcademyCraft.class);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(MusicSystem.class);
            MinecraftForge.EVENT_BUS.register(ClientRuntime.Events.class);
            MinecraftForge.EVENT_BUS.register(ClientHandler.ConfigHandler.class);
            MinecraftForge.EVENT_BUS.register(ACKeyManager.INSTANCE);
            MinecraftForge.EVENT_BUS.register(VanillaHandRenderer.EventHandler.class);
            MinecraftForge.EVENT_BUS.register(CriticalHitEffect.class);
            MinecraftForge.EVENT_BUS.register(ParticleFactoryBase.EventHandlers.class);
            MinecraftForge.EVENT_BUS.register(ControlOverrider.Events.class);
            MinecraftForge.EVENT_BUS.register(AuxGuiHandler.class);
            MinecraftForge.EVENT_BUS.register(CameraPosition.class);
        }
        MinecraftForge.EVENT_BUS.register(AcademyCraftRegister.class);
        MinecraftForge.EVENT_BUS.register(CPData.Events.class);
        MinecraftForge.EVENT_BUS.register(CooldownData.Events.class);
        MinecraftForge.EVENT_BUS.register(PresetData.Events.class);
        MinecraftForge.EVENT_BUS.register(WirelessSystem.class);
        MinecraftForge.EVENT_BUS.register(GameTimer.class);
        MinecraftForge.EVENT_BUS.register(AbilityPipeline.class);
        MinecraftForge.EVENT_BUS.register(MDDamageHelper.Events.class);
    }
}
