package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.entity.EntityMdRaySmall;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SBNetDelegate {
    public static final SBNetDelegate INSTANCE = new SBNetDelegate();
    public static final String MSG_EFFECT = "eff";

    public static void init() {
        NetworkS11n.addDirectInstance(SBNetDelegate.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel = MSG_EFFECT, side = Side.CLIENT)
    private void hSpawnEffect(Vec3d start, Vec3d end) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EntityMdRaySmall raySmall = new EntityMdRaySmall(player.world);
        raySmall.setFromTo(start, end);
        raySmall.viewOptimize = false;
        player.world.spawnEntity(raySmall);
    }
}
