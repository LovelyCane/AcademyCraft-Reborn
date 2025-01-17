package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.entity.EntitySilbarn;
import cn.lambdalib2.render.obj.ObjLegacyRender;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntitySilbarn extends Render<EntitySilbarn> {
    private final ObjLegacyRender model = Resources.getModel("silbarn");
    private final ResourceLocation tex = Resources.getTexture("models/silbarn");

    public RenderEntitySilbarn(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntitySilbarn var1, double x, double y, double z, float var8, float var9) {
        if (var1.hit)
            return;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        RenderUtils.loadTexture(tex);
        double scale = .05;
        GL11.glScaled(scale, scale, scale);
        GL11.glRotated(0.03 * (long) ((GameTimer.getTime() - var1.createTime) * 1000), var1.axis.x, var1.axis.y, var1.axis.z);
        GL11.glRotated(-var1.rotationYaw, 0, 1, 0);
        GL11.glRotated(90, 1, 0, 0);
        model.renderAll();
        GL11.glPopMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySilbarn entity) {
        return null;
    }
}
