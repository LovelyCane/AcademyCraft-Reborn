package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.WaveEffect;
import cn.academy.internal.datapart.HandRenderOverrideData;
import cn.academy.internal.client.renderer.util.IHandRenderer;
import cn.academy.internal.client.renderer.util.VanillaHandRenderer;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.VecUtils;
import cn.lambdalib2.vis.animation.presets.CompTransformAnim;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

import static cn.academy.internal.client.renderer.util.AnimPresets.createPrepareAnim;
import static cn.academy.internal.client.renderer.util.AnimPresets.createPunchAnim;
import static cn.lambdalib2.util.RandUtils.rangef;
import static cn.lambdalib2.util.RandUtils.rangei;

@SideOnly(Side.CLIENT)
@RegClientContext(BlastwaveContext.class)
@SuppressWarnings("unused")
public class BlastwaveContextC extends ClientContext {
    public static final String MSG_EFFECT = "effect";
    public static final String MSG_PERFORM = "perform";

    private IHandRenderer handEffect;
    private CompTransformAnim anim;
    private Supplier<Double> timeProvider;

    public BlastwaveContextC(BlastwaveContext par) {
        super(par);
    }

    @Listener(channel = MSG_EFFECT, side = {Side.CLIENT})
    private void effectAt(Vec3d pos) {
        ACSounds.playClient(world(), pos.x, pos.y, pos.z, "vecmanip.directed_blast", SoundCategory.AMBIENT, 0.5f, 1.0f);

        WaveEffect effect = new WaveEffect(world(), rangei(2, 3), 1);
        Vec3d headPosition = VecUtils.entityHeadPos(player);
        effect.setPosition(
                MathUtils.lerp(headPosition.x, pos.x, 0.7),
                MathUtils.lerp(headPosition.y, pos.y, 0.7),
                MathUtils.lerp(headPosition.z, pos.z, 0.7)
        );
        effect.rotationYaw = player.rotationYawHead + rangef(-20, 20);
        effect.rotationPitch = player.rotationPitch + rangef(-10, 10);

        world().spawnEntity(effect);
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = {Side.CLIENT})
    private void l_handEffectStart() {
        if (isLocal()) {
            anim = createPrepareAnim();

            final double init = GameTimer.getTime();
            timeProvider = () -> {
                double dt = GameTimer.getTime() - init;
                return Math.min(2.0, dt / 0.150);
            };

            handEffect = partialTicks -> {
                anim.perform(timeProvider.get());
                VanillaHandRenderer.renderHand(partialTicks, anim.target);
            };

            HandRenderOverrideData.get(player).addInterrupt(handEffect);
        }
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = {Side.CLIENT})
    private void l_handEffectTerminate() {
        if (isLocal()) {
            HandRenderOverrideData.get(player).stopInterrupt(handEffect);
        }
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel = MSG_PERFORM, side = {Side.CLIENT})
    private void l_effect() {
        if (isLocal()) {
            final double init = GameTimer.getTime();
            timeProvider = () -> {
                double dt = GameTimer.getTime() - init;
                return dt / 0.3;
            };

            anim = createPunchAnim();
            anim.perform(0);
        }
    }
}