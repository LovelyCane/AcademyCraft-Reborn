package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityArc;
import cn.academy.internal.entity.EntitySurroundArc;
import cn.academy.internal.client.renderer.util.ACRenderingHelper;
import cn.academy.internal.client.renderer.util.ArcPatterns;
import cn.academy.internal.sound.ACSounds;
import cn.academy.internal.sound.FollowEntitySound;
import cn.academy.internal.support.EnergyBlockHelper;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.ability.vanilla.electromaster.skill.ChargingBlockContext.MSG_EFFECT_END;
import static cn.academy.internal.ability.vanilla.electromaster.skill.ChargingBlockContext.MSG_EFFECT_START;

@SideOnly(Side.CLIENT)
@RegClientContext(ChargingContext.class)
@SuppressWarnings("unused")
public class ChargingContextC extends ClientContext {
    private EntityArc arc;
    private EntitySurroundArc surround;
    private FollowEntitySound sound;
    private boolean isItem = false;
    ChargingContext par;

    public ChargingContextC(ChargingContext par) {
        super(par);
        this.par = par;
    }

    @Listener(channel = MSG_EFFECT_START, side = Side.CLIENT)
    private void c_startEffects(boolean isItem) {
        if (!isItem) {
            arc = new EntityArc(player, ArcPatterns.chargingArc);
            arc.lengthFixed = (false);
            arc.hideWiggle = (0.8);
            arc.showWiggle = (0.2);
            arc.texWiggle = (0.8);
            player.world.spawnEntity(arc);

            surround = new EntitySurroundArc(player.world, player.posX, player.posY, player.posZ, 1, 1).setArcType(EntitySurroundArc.ArcType.NORMAL).setLife(100000);
            Debug.require(player.world.spawnEntity(surround));

            sound = new FollowEntitySound(player, "em.charge_loop", SoundCategory.AMBIENT).setLoop().setVolume(0.3f);
            ACSounds.playClient(sound);
        } else {
            sound = new FollowEntitySound(player, "em.charge_loop", SoundCategory.AMBIENT).setLoop().setVolume(0.3f);
            ACSounds.playClient(sound);
            surround = new EntitySurroundArc(player);
            surround.setArcType(EntitySurroundArc.ArcType.THIN);
            surround.setLife(100000);
            Debug.require(player.world.spawnEntity(surround));
        }

        this.isItem = isItem;
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_updateEffects() {
        if (isItem)
            return;
        RayTraceResult pos = Raytrace.traceLiving(player, par.distance);

        boolean good = false;
        if (pos.typeOfHit == RayTraceResult.Type.BLOCK) {
            TileEntity tile = player.getEntityWorld().getTileEntity(pos.getBlockPos());
            if (EnergyBlockHelper.isSupported(tile)) {
                good = true;
            }
        }

        MovingObjectData mod = new MovingObjectData();
        mod.blockX = pos.getBlockPos().getX();
        mod.blockY = pos.getBlockPos().getY();
        mod.blockZ = pos.getBlockPos().getZ();
        mod.hitVec = pos.hitVec;
        mod.isEntity = pos.typeOfHit == RayTraceResult.Type.ENTITY;
        if (mod.isEntity) {
            mod.entityEyeHeight = pos.entityHit.getEyeHeight();
        }

        double x, y, z;
        if (!mod.isNull) {
            x = mod.hitVec.x;
            y = mod.hitVec.y;
            z = mod.hitVec.z;
            if (mod.isEntity) {
                y += mod.entityEyeHeight;
            }
        } else {
            Vec3d mo = VecUtils.add(player.getPositionVector(), VecUtils.multiply(player.getLookVec(), par.distance));
            x = mo.x;
            y = mo.y;
            z = mo.z;
        }
        if (arc != null) {
            arc.setFromTo(player.posX, player.posY + ACRenderingHelper.getHeightFix(player), player.posZ, x, y, z);
        }

        if (surround != null) {
            if (good) {
                surround.updatePos(mod.blockX + 0.5, mod.blockY, mod.blockZ + 0.5);
                surround.draw = (true);
            } else {
                surround.draw = (false);
            }
        }
    }

    @Listener(channel = MSG_EFFECT_END, side = Side.CLIENT)
    private void c_endEffects(boolean isItem) {
        if (!isItem) {
            if (surround != null)
                surround.setDead();
            if (arc != null)
                arc.setDead();
            if (sound != null)
                sound.stop();
        } else {
            if (sound != null)
                sound.stop();
            if (surround != null)
                surround.setDead();
        }
    }
}
