package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.entity.EntityMagHook;
import cn.lambdalib2.render.obj.ObjLegacyRender;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntityMagHook extends Render<EntityMagHook> {
    final ObjLegacyRender model = Resources.getModel("maghook"), model_open = Resources.getModel("maghook_open");

    final ResourceLocation texture = Resources.getTexture("models/maghook");

    public RenderEntityMagHook(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMagHook entity) {
        return null;
    }

    @Override
    public void doRender(EntityMagHook ent, double x, double y, double z, float a, float b) {
        ObjLegacyRender realModel = model;
        if (ent.isHit) {
            realModel = model_open;
            ent.preRender();
            x = ent.posX - renderManager.viewerPosX;
            y = ent.posY - renderManager.viewerPosY;
            z = ent.posZ - renderManager.viewerPosZ;
        }

        GL11.glPushMatrix();
        RenderUtils.loadTexture(texture);
        GL11.glTranslated(x, y, z);
        GL11.glRotated(-ent.rotationYaw + 90, 0, 1, 0);
        GL11.glRotated(ent.rotationPitch - 90, 0, 0, 1);
        double scale = 0.0054;
        GL11.glScaled(scale, scale, scale);
        realModel.renderAll();
        GL11.glPopMatrix();
    }
}