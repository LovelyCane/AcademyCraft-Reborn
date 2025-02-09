package cn.lambdalib2.render.legacy;

import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


/**
 * Most commonly used material that can almost handle all the situations within MC.
 * Supports light disabling, default alpha blending and pure-colored drawing.
 *
 * @author WeAthFolD
 */
public class SimpleMaterial extends LegacyMaterial {
    public boolean ignoreLight = false;

    public SimpleMaterial(ResourceLocation _texture) {
        setTexture(_texture);
    }

    public SimpleMaterial setIgnoreLight() {
        ignoreLight = true;
        return this;
    }

    @Override
    public void onRenderStage(RenderStage stage) {
        if (stage == RenderStage.BEFORE_TESSELLATE) {
            GL11.glEnable(GL11.GL_BLEND);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Colors.bindToGL(color);

            if (mainTexture != null) {
                RenderUtils.loadTexture(mainTexture);
            } else {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
            }

            if (ignoreLight) {
                GL11.glDisable(GL11.GL_LIGHTING);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            }
        } else if (stage == RenderStage.END) {
            if (ignoreLight) {
                GL11.glEnable(GL11.GL_LIGHTING);
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }
}
