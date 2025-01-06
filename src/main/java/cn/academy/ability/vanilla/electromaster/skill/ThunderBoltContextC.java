package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.entity.EntityArc;
import cn.academy.internel.render.util.ArcPatterns;
import cn.academy.internel.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.entityx.handlers.Life;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.ability.vanilla.electromaster.skill.ThunderBolt.RANGE;

@SideOnly(Side.CLIENT)
@RegClientContext(ThunderBoltContext.class)
@SuppressWarnings("unused")
public class ThunderBoltContextC extends ClientContext {
    public ThunderBoltContextC(ThunderBoltContext par) {
        super(par);
    }

    @Listener(channel = ThunderBoltContext.MSG_PERFORM, side = Side.CLIENT)
    private void c_spawnEffect(AttackData ad) {
        // Spawn main arcs
        for (int i = 0; i <= 2; i++) {
            EntityArc mainArc = new EntityArc(player, ArcPatterns.strongArc);
            mainArc.length=(RANGE);
            player.getEntityWorld().spawnEntity(mainArc);
            mainArc.addMotionHandler(new Life(20));
        }

        // Spawn AOEs
        for (Entity e : ad.aoes) {
            EntityArc aoeArc = new EntityArc(player, ArcPatterns.aoeArc);
            aoeArc.lengthFixed = (false);
            aoeArc.setFromTo(ad.point.x, ad.point.y, ad.point.z, e.posX, e.posY + e.getEyeHeight(), e.posZ);
            aoeArc.addMotionHandler(new Life(RandUtils.rangei(15, 25)));
            player.getEntityWorld().spawnEntity(aoeArc);
        }

        // Play sound
        ACSounds.playClient(player, "em.arc_strong", SoundCategory.AMBIENT, 0.6f);
    }
}