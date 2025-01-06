package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.internel.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.ability.vanilla.electromaster.skill.MineDetect.TIME;

@SideOnly(Side.CLIENT)
@RegClientContext( MDContext.class)
@SuppressWarnings("unused")
public class MDContextC extends ClientContext {
    public MDContextC(MDContext par) {
        super(par);
    }

    @Listener(channel = MDContext.MSG_EFFECT, side = Side.CLIENT)
    private void c_spawnEffects(float range, boolean advanced) {
        if (isLocal()) {
            player.getEntityWorld().spawnEntity(new HandlerEntity(player, TIME, range, advanced));
            ACSounds.playClient(player, "em.minedetect", SoundCategory.AMBIENT, 0.5f);
        }
    }
}
