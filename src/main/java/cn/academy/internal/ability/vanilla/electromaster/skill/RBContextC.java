package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.meltdowner.skill.RBContext;
import cn.academy.internal.entity.EntityBarrageRayPre;
import cn.academy.internal.entity.EntityMdRayBarrage;
import cn.academy.internal.entity.EntitySilbarn;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(RBContext.class)
@SuppressWarnings("unused")
public class RBContextC extends ClientContext {
    public RBContextC(RBContext par) {
        super(par);
    }

    @Listener(channel = RBContext.MSG_EFFECT_PRERAY, side = Side.CLIENT)
    private void c_spawnPreRay(double x0, double y0, double z0, double x1, double y1, double z1, boolean hit) {
        EntityBarrageRayPre raySmall = new EntityBarrageRayPre(player.world, hit);
        raySmall.setFromTo(x0, y0 + 1.6, z0, x1, y1, z1);
        player.world.spawnEntity(raySmall);
    }

    @Listener(channel = RBContext.MSG_EFFECT_BARRAGE, side = Side.CLIENT)
    private void c_spawnBarrage(EntitySilbarn silbarn) {
        player.world.spawnEntity(new EntityMdRayBarrage(player.world, silbarn.posX, silbarn.posY, silbarn.posZ, player.rotationYaw, player.rotationPitch));
    }
}
