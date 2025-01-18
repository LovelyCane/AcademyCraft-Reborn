package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.Resources;
import cn.lambdalib2.render.*;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WaveEffectUI {
    private static class Ripple {
        float life;
        Vector2f pos;
        double timeAlive;
        float size;

        public Ripple(float life, Vector2f pos, double timeAlive, float size) {
            this.life = life;
            this.pos = pos;
            this.timeAlive = timeAlive;
            this.size = size;
        }

        public float getAlpha() {
            float prog = (float) (timeAlive / life);
            if (prog < 0.2f) {
                return prog / 0.2f;
            } else if (prog < 0.5f) {
                return 1;
            } else {
                return 1 - (prog - 0.5f) / 0.5f;
            }
        }

        public float getRealSize() {
            return size + (float) (timeAlive * 20);
        }
    }

    private final float maxAlpha;
    private final float avgSize;
    private final float intensity;

    private double lastFrameTime;
    private final List<Ripple> rippleList;
    private final RenderPass pass;
    private final RenderMaterial material;
    private final Mesh mesh;

    public WaveEffectUI(float maxAlpha, float avgSize, float intensity) {
        this.maxAlpha = maxAlpha;
        this.avgSize = avgSize;
        this.intensity = intensity;
        this.rippleList = new LinkedList<>();
        this.pass = new RenderPass();
        this.material = new RenderMaterial(ShaderScript.load(Resources.getShader("vm_wave.glsl")));
        this.mesh = new Mesh();

        // Initialize mesh
        mesh.setVertices(new Vector3f[]{
                v(0, 0), v(0, 1), v(1, 1), v(1, 0)
        });

        mesh.setUVsVec2(0, new Vector2f[]{
                vu(0, 0), vu(0, 1), vu(1, 1), vu(1, 0)
        });

        mesh.setIndices(new int[]{3, 2, 0, 2, 1, 0});

        material.setTexture("tex", Texture2D.load(Resources.getTexture("effects/glow_circle"), new TextureImportSettings(TextureImportSettings.FilterMode.Blinear, TextureImportSettings.WrapMode.Clamp)));
    }

    private Vector3f v(float x, float y) {
        return new Vector3f(x - 0.5f, y - 0.5f, 0);
    }

    private Vector2f vu(float x, float y) {
        return new Vector2f(x, y);
    }

    public void onFrame(float width, float height) {
        double timeStamp = currentTime();
        double deltaTime = timeStamp - lastFrameTime;

        update(deltaTime, width, height);

        RenderUtils.pushTextureState();
        render(width, height);
        RenderUtils.popTextureState();

        lastFrameTime = timeStamp;
    }

    private void update(double deltaTime, float width, float height) {
        // Update existing ripples
        Iterator<Ripple> iterator = rippleList.iterator();
        while (iterator.hasNext()) {
            Ripple ripple = iterator.next();
            ripple.timeAlive += deltaTime;

            if (ripple.timeAlive >= ripple.life) {
                iterator.remove();
            }
        }

        // Spawn new ripples
        if (RandUtils.nextFloat() < deltaTime * intensity) {
            float rSize = RandUtils.rangef(0.8f, 1.2f) * avgSize;
            float rLife = RandUtils.rangef(1.5f, 2.5f);
            Vector2f pos = new Vector2f(RandUtils.nextFloat() * width, RandUtils.nextFloat() * height);

            Ripple ripple = new Ripple(rLife, pos, 0, rSize);
            rippleList.add(ripple);
        }
    }

    private void render(float width, float height) {
        material.setVec2("screenSize", new Vector2f(width, height));

        for (Ripple ripple : rippleList) {
            InstanceData instance = new InstanceData();
            instance.setVec2("offset", ripple.pos);
            instance.setFloat("size", ripple.getRealSize());
            instance.setFloat("alpha", maxAlpha * ripple.getAlpha());
            pass.draw(material, mesh, instance);
        }

        pass.dispatch();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
    }

    private double currentTime() {
        return GameTimer.getTime();
    }
}
