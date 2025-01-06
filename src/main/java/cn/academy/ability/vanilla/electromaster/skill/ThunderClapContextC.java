package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.entity.EntityRippleMark;
import cn.academy.entity.EntitySurroundArc;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.entityx.EntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(ThunderClapContext.class)
@SuppressWarnings("unused")
public class ThunderClapContextC extends ClientContext {
    final String MSG_EFFECT_START = "effect_start";
    private EntitySurroundArc surroundArc;
    private EntityRippleMark mark;
    private int ticks = 0;
    private double hitX, hitY, hitZ;
    private boolean canTicking = false;

    public ThunderClapContextC(ThunderClapContext par) {
        super(par);
    }

    @NetworkMessage.Listener(channel = MSG_EFFECT_START, side = Side.CLIENT)
    private void c_spawnEffect() {
        canTicking = true;
        surroundArc = new EntitySurroundArc(player).setArcType(EntitySurroundArc.ArcType.BOLD);
        player.getEntityWorld().spawnEntity(surroundArc);

        if (isLocal()) {
            mark = new EntityRippleMark(player.world);
            player.getEntityWorld().spawnEntity(mark);
            mark.color.set(204, 204, 204, 179);
            mark.setPosition(hitX, hitY, hitZ);
        }
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_updateEffect() {
        if (canTicking) {
            final double DISTANCE = 40.0;
            RayTraceResult pos = Raytrace.traceLiving(player, DISTANCE, EntitySelectors.nothing());
            hitX = pos.hitVec.x;
            hitY = pos.hitVec.y;
            hitZ = pos.hitVec.z;

            ticks++;
            if (isLocal()) {
                float max = 0.1f;
                float min = 0.001f;
                player.capabilities.setPlayerWalkSpeed(Math.max(min, max - (max - min) / 60 * ticks));
                if (mark != null)
                    mark.setPosition(hitX, hitY, hitZ);
            }
        }
    }

    @NetworkMessage.Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_terminated() {
        canTicking = false;
        player.capabilities.setPlayerWalkSpeed(0.1f);
        if (surroundArc != null) {
            surroundArc.executeAfter((EntityCallback<Entity>) Entity::setDead, 10);
        }

        if (isLocal() && mark != null) {
            mark.setDead();
        }
    }
}
