package cn.academy.internal.client.render.entity.ray;

import cn.academy.internal.entity.IRay;
import cn.academy.internal.client.render.entity.RendererList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * @author WeAthFolD
 */
public class RendererRayComposite extends RendererList {

    public RendererRayGlow glow;
    public RendererRayCylinder cylinderIn, cylinderOut;

    public RendererRayComposite(RenderManager manager, String name) {
        super(manager);
        append(glow = RendererRayGlow.createFromName(manager, name));
        append(cylinderIn = new RendererRayCylinder(manager, 0.05f));
        append(cylinderOut = new RendererRayCylinder(manager, 0.08f));
        cylinderIn.headFix = 0.98;
    }

    @Override
    public void doRender(Entity ent, double x, double y, double z, float a, float b) {
        ((IRay) ent).onRenderTick();
        super.doRender(ent, x, y, z, a, b);
    }

    public void plainDoRender(Entity ent, double x, double y, double z, float a, float b) {
        super.doRender(ent, x, y, z, a, b);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }
}