package cn.academy.internal.client.renderer.entity.ray;

import cn.academy.internal.client.renderer.entity.RenderList;
import cn.academy.internal.entity.IRay;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

/**
 * @author WeAthFolD
 */
public class RenderRayComposite extends RenderList<Entity> {
    public RendererRayGlow<IRay> glow;
    public RendererRayCylinder cylinderIn, cylinderOut;

    public RenderRayComposite(RenderManager manager, String name) {
        super(manager);
        append(glow = RendererRayGlow.createFromName(manager, name));
        append(cylinderIn = new RendererRayCylinder(manager, 0.05f));
        append(cylinderOut = new RendererRayCylinder(manager, 0.08f));
        cylinderIn.headFix = 0.98;
    }

    @Override
    public void doRender(@Nonnull Entity ent, double x, double y, double z, float a, float b) {
        ((IRay) ent).onRenderTick();
        super.doRender(ent, x, y, z, a, b);
    }

    public void plainDoRender(Entity ent, double x, double y, double z, float a, float b) {
        super.doRender(ent, x, y, z, a, b);
    }
}