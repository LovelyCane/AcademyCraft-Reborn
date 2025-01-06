package cn.academy.ability.vanilla.teleporter.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.entity.EntityTPMarking;
import cn.academy.internel.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(MTContext.class)
@SuppressWarnings("unused")
public class MTContextC extends ClientContext {
    final String MSG_SOUND = "sound";
    private EntityTPMarking mark;
    private int ticks = 0;
    MTContext par;

    public MTContextC(MTContext par) {
        super(par);
        this.par = par;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = {Side.CLIENT})
    private void l_start() {
        if (isLocal()) {
            mark = new EntityTPMarking(player);
            player.world.spawnEntity(mark);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = {Side.CLIENT})
    private void l_update() {
        if (mark == null)
            terminate();

        ticks += 1;
        Vec3d dest = par.getDest(player, ticks);
        if (isLocal())
            mark.setPosition(dest.x, dest.y, dest.z);
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = {Side.CLIENT})
    private void l_end() {
        if (isLocal())
            mark.setDead();
    }

    @Listener(channel = MSG_SOUND, side = {Side.CLIENT})
    private void c_sound() {
        ACSounds.playClient(player, "tp.tp", SoundCategory.AMBIENT, 0.5f);
    }
}