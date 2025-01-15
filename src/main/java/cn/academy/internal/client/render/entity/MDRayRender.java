package cn.academy.internal.client.render.entity;

import cn.academy.internal.client.render.entity.ray.RendererRayComposite;
import cn.academy.internal.entity.EntityMDRay;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.renderer.entity.RenderManager;

@RegEntityRender(EntityMDRay.class)
public class MDRayRender extends RendererRayComposite {
    public MDRayRender(RenderManager manager) {
        super(manager, "mdray");
        this.cylinderIn.width = 0.17;
        this.cylinderIn.color.set(216, 248, 216, 230);

        this.cylinderOut.width = 0.22;
        this.cylinderOut.color.set(106, 242, 106, 50);

        this.glow.width = 1.5;
        this.glow.color.setAlpha(Colors.f2i(0.8f));
    }
}