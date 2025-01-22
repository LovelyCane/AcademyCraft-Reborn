package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.client.renderer.entity.ray.RenderRayComposite;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityMineRayBasic extends RenderRayComposite {
    public RenderEntityMineRayBasic(RenderManager manager) {
        super(manager, "mdray_small");
        this.cylinderIn.width = 0.03;
        this.cylinderIn.color.set(216, 248, 216, 230);

        this.cylinderOut.width = 0.045;
        this.cylinderOut.color.set(106, 242, 106, 50);

        this.glow.width = 0.3;
        this.glow.color.setAlpha(Colors.f2i(0.5f));
    }
}