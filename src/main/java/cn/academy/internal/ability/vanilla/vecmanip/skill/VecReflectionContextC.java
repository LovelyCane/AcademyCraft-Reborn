package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.WaveEffect;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.WaveEffectUI;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflection.MSG_REFLECT_ENTITY;
import static cn.academy.internal.ability.vanilla.vecmanip.skill.VecReflectionContext.reflect;
import static cn.lambdalib2.util.VecUtils.entityHeadPos;

@SideOnly(Side.CLIENT)
@RegClientContext(VecReflectionContext.class)
public class VecReflectionContextC extends ClientContext {
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
        if (evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            ui.onFrame(evt.getResolution().getScaledWidth(), evt.getResolution().getScaledHeight());
        }
    }
}
