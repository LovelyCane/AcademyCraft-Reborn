package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.ability.vanilla.generic.client.effect.SmokeEffect;
import cn.academy.internal.client.CameraPosition;
import cn.lambdalib2.render.legacy.Tessellator;
import cn.lambdalib2.util.EntityLook;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSmokeEffect extends Render<SmokeEffect> {
    private final ResourceLocation texture = Resources.preloadTexture("effects/smokes");

    public RenderSmokeEffect(RenderManager m) {
        super(m);
    }

    @Override
    public void doRender(SmokeEffect eff, double x, double y, double z, float pt, float wtf) {
        Vec3d campos = CameraPosition.getVec3d();
        Vec3d delta = new Vec3d(x, y, z).subtract(campos);
        EntityLook look = new EntityLook(delta);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glRotated(-look.yaw + 180, 0, 1, 0);
        GL11.glRotated(-look.pitch, 1, 0, 0);
        GL11.glScaled(eff.size, eff.size, 1);

        double u = (eff.frame % 2.0) / 2.0;
        double v = (eff.frame / 2.0) / 2.0;

        Tessellator t = Tessellator.instance;
        GL11.glColor4f(1, 1, 1, eff.alpha());
        RenderUtils.loadTexture(texture);

        t.startDrawingQuads();
        t.addVertexWithUV(-1, -1, 0, u, v);
        t.addVertexWithUV(-1, 1, 0, u, v + 0.5);
        t.addVertexWithUV(1, 1, 0, u + 0.5, v + 0.5);
        t.addVertexWithUV(1, -1, 0, u + 0.5, v);
        t.draw();

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    @Override
    protected ResourceLocation getEntityTexture(SmokeEffect entity) {
        return null;
    }
}
