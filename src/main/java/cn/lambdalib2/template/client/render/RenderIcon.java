package cn.lambdalib2.template.client.render;

import cn.lambdalib2.render.legacy.Tessellator;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.RenderUtils;
import cn.lambdalib2.util.ViewOptimize;
import cn.lambdalib2.util.ViewOptimize.IAssociatePlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Color;

/**
 * A EntityRender that renders an entity as a single icon.
 */
@SideOnly(Side.CLIENT)
public class RenderIcon<T extends Entity> extends Render<T> {
    protected ResourceLocation icon;
    protected float size = 0.5F;
    protected boolean hasLight = false;
    public final Color color = Colors.white();
    
    protected float minTolerateAlpha = 0.0F; //The minium filter value of alpha test. Used in transparent texture adjustments.

    public RenderIcon(RenderManager manager, ResourceLocation ic) {
        super(manager);
        icon = ic;
    }
    
    public RenderIcon setSize(float s) {
        size = s;
        return this;
    }

    @Override
    public void doRender(T par1Entity, double par2, double par4,
            double par6, float par8, float par9) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_CULL_FACE);
            if(!hasLight)
                GL11.glDisable(GL11.GL_LIGHTING);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, minTolerateAlpha);
            GL11.glPushMatrix(); {
                GL11.glTranslatef((float) par2, (float) par4, (float) par6);
                GL11.glScalef(size, size, size);
                postTranslate();
                if(icon != null) RenderUtils.loadTexture(icon);
                
                Tessellator t = Tessellator.instance;
                this.func_77026_a(par1Entity, t);
                
            } GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
    }
    
    protected void postTranslate() {}
    
    protected void firstTranslate() {}

    private void func_77026_a(Entity e, Tessellator tessllator) {
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        if(!hasLight) 
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        firstTranslate();
        Colors.bindToGL(color);
        tessllator.startDrawingQuads();

        if(e instanceof IAssociatePlayer) {
            ViewOptimize.fix((IAssociatePlayer) e);
        }
        tessllator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, 0, 1);
        tessllator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, 1, 1);
        tessllator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, 1, 0);
        tessllator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, 0, 0);
        
        tessllator.draw();
    }


    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return icon;
    }
}
