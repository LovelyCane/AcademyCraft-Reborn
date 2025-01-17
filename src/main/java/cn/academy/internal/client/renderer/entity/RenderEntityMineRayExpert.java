package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.client.renderer.entity.ray.RenderRayComposite;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class RenderEntityMineRayExpert extends RenderRayComposite {
    public RenderEntityMineRayExpert(RenderManager manager) {
        super(manager, "mdray_expert");
        this.cylinderIn.width = 0.045;
        this.cylinderIn.color.set(216, 248, 216, 230);

        this.cylinderOut.width = 0.056;
        this.cylinderOut.color.set(106, 242, 106, 50);

        this.glow.width = 0.5;
        this.glow.color.setAlpha(Colors.f2i(0.7f));
    }

    @Override
    public void doRender(Entity ent, double x,
                         double y, double z, float a, float b) {
        this.cylinderIn.width = 0.045;
        this.cylinderIn.color.set(216, 248, 216, 180);

        this.cylinderOut.width = 0.056;
        this.cylinderOut.color.set(106, 242, 106, 50);

        this.glow.color.setAlpha(Colors.f2i(0.5f));
        super.doRender(ent, x, y, z, a ,b);
    }
}