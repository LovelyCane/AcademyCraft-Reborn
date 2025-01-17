package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.datapart.HandRenderOverrideData;
import cn.academy.internal.client.renderer.util.IHandRenderer;
import cn.academy.internal.client.renderer.util.VanillaHandRenderer;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.vis.animation.presets.CompTransformAnim;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

import static cn.academy.internal.ability.vanilla.vecmanip.skill.ShockContext.*;
import static cn.academy.internal.client.renderer.util.AnimPresets.createPrepareAnim;
import static cn.academy.internal.client.renderer.util.AnimPresets.createPunchAnim;

@SideOnly(Side.CLIENT)
@RegClientContext(ShockContext.class)
@SuppressWarnings("unused")
public class ShockContextC extends ClientContext {
    private IHandRenderer handEffect;
    private CompTransformAnim anim;
    private Supplier<Double> timeProvider;

    public ShockContextC(ShockContext par) {
        super(par);
    }

    @Listener(channel = MSG_GENERATE_EFFECT, side = Side.CLIENT)
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

    @Listener(channel = MSG_GENERATE_EFFECT, side = Side.CLIENT)
    private void c_effect() {
        ACSounds.playClient(player, "vecmanip.directed_shock", SoundCategory.AMBIENT, 0.5f);
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void l_handEffectStart() {
        if (isLocal()) {
            anim = createPrepareAnim();

            final double init = GameTimer.getTime();
            timeProvider = () -> {
                double dt = GameTimer.getTime() - init;
                return Math.min(2.0, dt / 0.15);
            };

            handEffect = partialTicks -> {
                anim.perform(timeProvider.get());
                VanillaHandRenderer.renderHand(partialTicks, anim.target);
            };
            HandRenderOverrideData.get(player).addInterrupt(handEffect);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void l_handEffectTerminate() {
        if (isLocal()) {
            HandRenderOverrideData.get(player).stopInterrupt(handEffect);
        }
    }
}
