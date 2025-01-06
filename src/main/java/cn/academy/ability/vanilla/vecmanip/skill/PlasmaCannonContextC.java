package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.ability.vanilla.vecmanip.client.effect.PlasmaBodyEffect;
import cn.academy.internel.sound.ACSounds;
import cn.academy.internel.sound.FollowEntitySound;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(PlasmaCannonContext.class)
@SuppressWarnings("unused")
public class PlasmaCannonContextC extends ClientContext {
    private FollowEntitySound sound;
    private PlasmaBodyEffect effect;
    PlasmaCannonContext self;

    public PlasmaCannonContextC(PlasmaCannonContext self) {
        super(self);
        this.self = self;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void c_begin() {
        effect = new PlasmaBodyEffect(world(), self);
        effect.setPosition(self.chargePosition.x, self.chargePosition.y, self.chargePosition.z);

        world().spawnEntity(new Tornado(self));
        world().spawnEntity(effect);

        sound = new FollowEntitySound(player, "vecmanip.plasma_cannon", SoundCategory.AMBIENT);
        ACSounds.playClient(sound);
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_terminate() {
        sound.stop();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_tick() {
        if (self.state == PlasmaCannonContext.STATE_GO) {
            self.tryMove();
        }
        effect.setPosition(self.chargePosition.x, self.chargePosition.y, self.chargePosition.z);
    }
}
