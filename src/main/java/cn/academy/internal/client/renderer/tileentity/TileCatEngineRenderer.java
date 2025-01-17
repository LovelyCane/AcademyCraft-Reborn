package cn.academy.internal.client.renderer.tileentity;

import cn.academy.Resources;
import cn.academy.internal.tileentity.TileCatEngine;
import cn.lambdalib2.render.legacy.Tessellator;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.FastTESR;
import org.lwjgl.opengl.GL11;

/**
 * @author WeAthFolD
 */
public class TileCatEngineRenderer extends FastTESR<TileCatEngine> {
    static final ResourceLocation TEXTURE = Resources.getTexture("blocks/cat_engine");

    @Override
    public void renderTileEntityFast(TileCatEngine te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        long time = (long) (GameTimer.getTime() * 1000);
        if (te.lastRender != 0) {
            te.rotation += (time - te.lastRender) * te.thisTickGen * 1e-2;
            te.rotation %= 360;
        }
        te.lastRender = time;

        x += 0.5;
        z += 0.5;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glTranslated(x, y + 0.03 * Math.sin(GameTimer.getTime() * 0.006), z);

        double yaw = Math.atan2(x, z) * 180 / Math.PI;
        GL11.glRotated(yaw + 180, 0, 1, 0);
        GL11.glTranslated(0, .5, 0);
        GL11.glRotated(te.rotation, 1, 0, 0);
        GL11.glTranslated(-.5, -.5, 0);

        Tessellator t = Tessellator.instance;
        RenderUtils.loadTexture(TEXTURE);
        t.startDrawingQuads();
        t.addVertexWithUV(0, 0, 0, 0, 0);
        t.addVertexWithUV(1, 0, 0, 1, 0);
        t.addVertexWithUV(1, 1, 0, 1, 1);
        t.addVertexWithUV(0, 1, 0, 0, 1);
        t.draw();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}