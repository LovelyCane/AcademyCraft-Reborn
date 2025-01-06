package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.RegClientContext;
import cn.academy.entity.EntityArc;
import cn.academy.internel.render.util.ACRenderingHelper;
import cn.academy.internel.render.util.ArcPatterns;
import cn.academy.internel.sound.ACSounds;
import cn.academy.internel.sound.FollowEntitySound;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.ability.vanilla.electromaster.skill.MovementContext.*;

@SideOnly(Side.CLIENT)
@RegClientContext(MovementContext.class)
@SuppressWarnings("unused")
public class MovementContextC extends ClientContext {

    private EntityArc arc;
    private FollowEntitySound sound;

    private static final String SOUND = "vecmanip.mov_context_sound";  // Define appropriate sound name

    public MovementContextC(MovementContext par) {
        super(par);
    }

    @Listener(channel = MSG_EFFECT_START, side = Side.CLIENT)
    private void c_startEffect() {
        arc = new EntityArc(player, ArcPatterns.thinContiniousArc);
        arc.lengthFixed = false;
        arc.texWiggle = 1;
        arc.showWiggle = 0.1;
        arc.hideWiggle = 0.6;

        player.getEntityWorld().spawnEntity(arc);

        sound = new FollowEntitySound(player, SOUND, SoundCategory.AMBIENT).setLoop();
        ACSounds.playClient(sound);
    }

    @Listener(channel = MSG_EFFECT_UPDATE, side = Side.CLIENT)
    private void c_updateEffect(Vec3d target) {
        arc.setFromTo(player.posX, player.posY + ACRenderingHelper.getHeightFix(player), player.posZ, target.x, target.y, target.z);
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_endEffect() {
        if (arc != null)
            arc.setDead();
        if (sound != null)
            sound.stop();
    }
}