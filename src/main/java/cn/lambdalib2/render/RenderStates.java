package cn.lambdalib2.render;

import static org.lwjgl.opengl.GL11.*;

public class RenderStates {
    public enum TestMode {
        Always(GL_ALWAYS),
        Never(GL_NEVER),
        NotEqual(GL_NOTEQUAL),
        Equal(GL_EQUAL),
        Greater(GL_GREATER),
        GEqual(GL_GEQUAL),
        Less(GL_LESS),
        LEqual(GL_LEQUAL);

        public final int func;

        TestMode(int glFunc) {
            func = glFunc;
        }
    }

    public enum BlendFunc {
        One(GL_ONE),            Zero(GL_ZERO),
        SrcColor(GL_SRC_COLOR), OneMinusSrcColor(GL_ONE_MINUS_SRC_COLOR),
        SrcAlpha(GL_SRC_ALPHA), OneMinusSrcAlpha(GL_ONE_MINUS_SRC_ALPHA),
        DstColor(GL_DST_COLOR), OneMinusDstColor(GL_ONE_MINUS_DST_COLOR),
        DstAlpha(GL_DST_ALPHA), OneMinusDstAlpha(GL_ONE_MINUS_DST_ALPHA);

        final int func;

        BlendFunc(int f) {
            func = f;
        }
    }

    public enum CullMode {
        Back(GL_BACK), Front(GL_FRONT), Off(-1);

        public final int func;

        CullMode(int f) {
            func = f;
        }
    }

    public TestMode depthTestMode = TestMode.Always;

    public boolean depthMask = true;

    public TestMode alphaTestMode = TestMode.Always;
    public float alphaTestRef = 0.1f;

    public CullMode cullMode = CullMode.Back;

    public boolean
            colorMaskR = true,
            colorMaskG = true,
            colorMaskB = true,
            colorMaskA = true;

    public boolean blending = false;
    public BlendFunc srcBlend = BlendFunc.SrcAlpha, dstBlend = BlendFunc.OneMinusSrcAlpha;

    public void apply() {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(depthTestMode.func);
        glDepthMask(depthMask);

        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(alphaTestMode.func, alphaTestRef);

        if (cullMode == CullMode.Off) {
            glDisable(GL_CULL_FACE);
        } else {
            glEnable(GL_CULL_FACE);
            glCullFace(cullMode.func);
        }

        glColorMask(colorMaskR, colorMaskG, colorMaskB, colorMaskA);

        if (blending) {
            glEnable(GL_BLEND);
            glBlendFunc(srcBlend.func, dstBlend.func);
        } else {
            glDisable(GL_BLEND);
        }
    }
}
