package cn.academy.entity;


import cn.academy.AcademyCraft;
import cn.academy.internel.render.entity.ray.RendererRayComposite;
import cn.academy.internel.render.util.ArcFactory;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@RegEntityRender(EntityRailgunFX.class)
public class RailgunRender extends RendererRayComposite {
    ArcFactory.Arc[] arcs;
    static final int ARC_SIZE = 15;

    public RailgunRender(RenderManager manager) {
        super(manager, "railgun");
        glow.startFix = -0.3;
        glow.endFix = 0.3;
        glow.width = 1.1;

        cylinderIn.color.set(241, 240, 222, 200);
        cylinderIn.width = 0.09;

        cylinderOut.color.set(236, 170, 93, 60);
        cylinderOut.width = 0.13;

        ArcFactory factory = new ArcFactory();
        factory.widthShrink = 0.9;
        factory.maxOffset = 0.8;
        factory.passes = 3;
        factory.width = 0.3;
        factory.branchFactor = 0.7;

        arcs = new ArcFactory.Arc[ARC_SIZE];
        for (int i = 0; i < ARC_SIZE; ++i) {
            arcs[i] = factory.generate(RandUtils.ranged(2, 3));
        }
    }

    @Override
    public void doRender(Entity ent, double x, double y, double z, float a, float b) {
        AcademyCraft.log.info("Rendering " + ent);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        ViewOptimize.fix((ViewOptimize.IAssociatePlayer) ent);

        EntityRailgunFX railgun = (EntityRailgunFX) ent;

        railgun.arcHandler.drawAll();

        GL11.glPopMatrix();

        super.doRender(ent, x, y, z, a, b);
    }

}

