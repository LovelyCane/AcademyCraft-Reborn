package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.generic.client.effect.BloodSprayEffect;
import cn.academy.internal.entity.EntityBloodSplash;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.BlockSelectors;
import cn.lambdalib2.util.EntityLook;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

import static cn.lambdalib2.util.RandUtils.*;

@SideOnly(Side.CLIENT)
@RegClientContext(BloodRetroContext.class)
@SuppressWarnings("unused")
class BloodRetroContextC extends ClientContext {
    final String MSG_PERFORM = "perform";

    public BloodRetroContextC(BloodRetroContext par) {
        super(par);
    }

    @NetworkMessage.Listener(channel = MSG_PERFORM, side = Side.CLIENT)
    private void c_perform(EntityLivingBase targ) {
        for (int i = 0; i < rangei(6, 10); i++) {
            EntityBloodSplash splash = new EntityBloodSplash(world());
            splash.setSize(rangef(1.4f, 1.8f));
            Vec3d dv = new Vec3d(ranged(-1, 1) * targ.width, ranged(0, 1) * targ.height, ranged(-1, 1) * targ.width);
            splash.setPosition(targ.posX + dv.x + player.getLookVec().x * 0.2, targ.posY + dv.y + player.getLookVec().y * 0.2, targ.posZ + dv.z + player.getLookVec().z * 0.2);
            world().spawnEntity(splash);
        }

        Vec3d headPos = targ.getPositionVector().add(0, targ.height * 0.6, 0);
        List<Vec3d> looks = Arrays.asList(new EntityLook(player.rotationYawHead + rangef(-20, 20), 0).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), 30).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), 45).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), 60).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), 80).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), -30).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), -45).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), -60).toVec3(), new EntityLook(player.rotationYawHead + rangef(-20, 20), -80).toVec3());
        for (Vec3d look : looks) {
            RayTraceResult r = Raytrace.perform(world(), new Vec3d(headPos.x - look.x * 0.5, headPos.y - look.y * 0.5, headPos.z - look.z * 0.5), new Vec3d(headPos.x + look.x * 5, headPos.y + look.y * 5, headPos.z + look.z * 5), EntitySelectors.nothing(), BlockSelectors.filNormal);
            if (r.typeOfHit == RayTraceResult.Type.BLOCK) {
                for (int i = 0; i < rangei(2, 3); i++) {
                    BloodSprayEffect spray = new BloodSprayEffect(world(), r.getBlockPos(), r.sideHit.getIndex());
                    world().spawnEntity(spray);
                }
            }
        }

        ACSounds.playClient(player, "vecmanip.blood_retro", SoundCategory.AMBIENT, 1F);
    }
}