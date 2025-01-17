package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.entity.EntityArc;
import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntityArc extends Render<EntityArc> {
    public RenderEntityArc(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityArc arc, double x, double y, double z, float f, float g) {
        if (!arc.show)
            return;

        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotatef(arc.rotationYaw + 90, 0, -1, 0);
        GL11.glRotatef(arc.rotationPitch, 0, 0, -1);

        if (arc.viewOptimize) {
            ViewOptimize.fix(arc);
        }

        if (arc.lengthFixed) {
            for (int i = 0; i < arc.n; ++i)
                arc.patterns[arc.iid[i]].draw();
        } else {
            for (int i = 0; i < arc.n; ++i) {
                arc.patterns[arc.iid[i]].draw(arc.length);
            }
        }

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityArc entity) {
        return null;
    }
}