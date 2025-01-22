package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.entity.EntitySurroundArc;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntitySurroundArc extends Render<EntitySurroundArc> {
    public RenderEntitySurroundArc(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntitySurroundArc esa, double x, double y, double z, float a, float b) {
        if (esa.draw && esa.arcHandler != null) {
            GL11.glPushMatrix();

            GL11.glTranslated(x, y, z);

            GL11.glRotatef(-esa.rotationYaw, 0, 1, 0);
            esa.arcHandler.drawAll();

            GL11.glPopMatrix();
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySurroundArc entity) {
        return null;
    }
}