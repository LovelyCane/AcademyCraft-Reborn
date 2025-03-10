package cn.lambdalib2.particle;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * A delegator renderer for Sprite and Entities that implements ISpriteEntity.
 *
 * @author WeAthFolD
 */
public class RenderParticle<T extends Particle> extends Render<T> {
    static Sprite sprite = new Sprite();

    public RenderParticle(RenderManager m) {
        super(m);
        this.shadowOpaque = 0;
    }

    @Override
    public void doRender(T ent, double x, double y, double z, float a, float b) {
        if (!ent.updated)
            return;

        ent.updateSprite(sprite);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
        GL11.glPushMatrix();

        if (ent.needViewOptimize()) {
            GL11.glTranslated(0, -0.2, 0);
        }

        GL11.glTranslated(x, y, z);
        if (ent.customRotation) {
            GL11.glRotatef(ent.rotationYaw, 0, 1, 0);
            GL11.glRotatef(ent.rotationPitch, 0, 0, 1);
        } else {
            GL11.glRotatef(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        }

        sprite.draw();

        GL11.glPopMatrix();
        GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T p_110775_1_) {
        return null;
    }
}
