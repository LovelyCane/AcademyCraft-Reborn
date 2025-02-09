package cn.lambdalib2.cgui.component;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.HudUtils;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Draws a squared texture that fills the area of the given widget.
 */
public class DrawTexture extends Component {
    public static final ResourceLocation MISSING = new ResourceLocation("lambdalib2:textures/cgui/missing.png");

    public enum DepthTestMode {Default, Equals}

    public ResourceLocation texture;

    public Color color;

    public double zLevel = 0;

    public boolean writeDepth = true;

    public boolean doesUseUV;

    public double u = 0, v = 0, texWidth = 0, texHeight = 0;

    public DepthTestMode depthTestMode = DepthTestMode.Default;

    private final int shaderId = 0;

    public DrawTexture() {
        this(MISSING);
    }

    public DrawTexture(ResourceLocation texture) {
        this(texture, Colors.white());
    }

    public DrawTexture(String name, ResourceLocation _texture, Color _color) {
        super(name);
        this.texture = _texture;
        this.color = _color;

        listen(FrameEvent.class, (w, e) -> {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_ALPHA_TEST);
            glDepthMask(writeDepth);
            glUseProgram(shaderId);
            if (depthTestMode == DepthTestMode.Equals) {
                GL11.glEnable(GL_DEPTH_TEST);
                GL11.glDepthFunc(GL_EQUAL);
            } else if (writeDepth) {
                GL11.glEnable(GL_DEPTH_TEST);
                GL11.glDepthFunc(GL_ALWAYS);
            } else {
                GL11.glDisable(GL_DEPTH_TEST);
            }

            Colors.bindToGL(color);

            if (zLevel != 0) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0, zLevel);
            }

            if (texture != null && !texture.getPath().equals("<null>")) {
                HudUtils.loadTexture(texture);
                if (doesUseUV) {
                    HudUtils.rect(0, 0, u, v, w.transform.width, w.transform.height, texWidth, texHeight);
                } else {
                    HudUtils.rect(0, 0, w.transform.width, w.transform.height);
                }
            } else {
                HudUtils.colorRect(0, 0, w.transform.width, w.transform.height);
            }

            if (zLevel != 0) {
                GL11.glPopMatrix();
            }

            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDepthFunc(GL_LEQUAL);

            glUseProgram(0);
            glDepthMask(true);
        });
    }

    public DrawTexture(ResourceLocation _texture, Color _color) {
        this("DrawTexture", _texture, _color);
    }

    public DrawTexture setTex(ResourceLocation t) {
        texture = t;
        return this;
    }

    /**
     * Set the color as a **copy** of the given color.
     */
    public DrawTexture setColor(Color c) {
        this.color.setColor(c);
        return this;
    }

    public static DrawTexture get(Widget w) {
        return w.getComponent(DrawTexture.class);
    }
}
