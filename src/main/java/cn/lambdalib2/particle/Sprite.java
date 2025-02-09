package cn.lambdalib2.particle;

import static org.lwjgl.opengl.GL11.*;

import cn.lambdalib2.render.legacy.ShaderSimple;
import cn.lambdalib2.render.legacy.Tessellator;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.RenderUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL20;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;

/**
 * Represents a drawable sprite in origin. Always face (0, 0, -1).
 * 
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public final class Sprite {
    /**
     * If the texture is null draw pure-colored sprite.
     */
    public ResourceLocation texture;
    public float width = 1.0f, height = 1.0f;
    public Color color = Colors.white();
    public boolean hasLight = false;
    public boolean cullFace = true;


    public Sprite setTexture(ResourceLocation rl) {
        texture = rl;
        return this;
    }

    public Sprite setSize(float w, float h) {
        width = w;
        height = h;
        return this;
    }

    public Sprite setColor(Color nc) {
        color = nc;
        return this;
    }

    public void draw() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (texture != null) {
            RenderUtils.loadTexture(texture);
        } else {
            glDisable(GL_TEXTURE_2D);
        }

        if (!cullFace) {
            glDisable(GL_CULL_FACE);
        }

        Colors.bindToGL(color);
        Tessellator t = Tessellator.instance;
        float hw = width / 2, hh = height / 2;

        if (hasLight) {
            t.startDrawingQuads();
            t.setNormal(0, 0, -1);
            t.addVertexWithUV(-hw, hh, 0, 0, 0);
            t.addVertexWithUV(-hw, -hh, 0, 0, 1);
            t.addVertexWithUV(hw, -hh, 0, 1, 1);
            t.addVertexWithUV(hw, hh, 0, 1, 0);
            t.draw();
        } else {
            // Use legacy routine to avoid ShaderMod to ruin the render
            ShaderSimple.instance().useProgram();
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex3f(-hw, hh, 0);
            glTexCoord2f(0, 1);
            glVertex3f(-hw, -hh, 0);
            glTexCoord2f(1, 1);
            glVertex3f(hw, -hh, 0);
            glTexCoord2f(1, 0);
            glVertex3f(hw, hh, 0);
            glEnd();
            GL20.glUseProgram(0);
        }

        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
    }

}
