package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.client.renderer.entity.ray.RenderRayComposite;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityMineRayLuck extends RenderRayComposite {
    public RenderEntityMineRayLuck(RenderManager manager) {
        super(manager, "mdray_luck");
        this.cylinderIn.width = 0.04;
        this.cylinderIn.color.set(241, 229, 247, 230);

        this.cylinderOut.width = 0.05;
        this.cylinderOut.color.set(205, 166, 232, 50);

        this.glow.width = 0.45;
        this.glow.color.setAlpha(Colors.f2i(0.6f));
    }
}