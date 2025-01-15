package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.ParabolaEffect;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(VecAccelContext.class)
@SuppressWarnings("unused")
public class VecAccelContextC extends ClientContext {
    final String MSG_PERFORM = "perform";
    VecAccelContext par;
    public VecAccelContextC(VecAccelContext par) {
        super(par);
        this.par = par;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = {Side.CLIENT})
    private void l_makeAlive() {
        if (isLocal()) {
            world().spawnEntity(new ParabolaEffect(par));
        }
    }

    @Listener(channel = MSG_PERFORM, side = {Side.CLIENT})
    private void c_perform() {
        ACSounds.playClient(player, "vecmanip.vec_accel", SoundCategory.AMBIENT, 0.35f);
    }
}
