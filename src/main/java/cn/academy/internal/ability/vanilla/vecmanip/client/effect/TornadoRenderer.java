package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.Resources;
import cn.academy.internal.util.ImprovedNoise;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

@SideOnly(Side.CLIENT)
public class TornadoRenderer {
    private static final ResourceLocation TEXTURE = Resources.getTexture("effects/tornado_ring");
    private static final int DIV = 20;
    private static final double U_STEP = 1.0 / DIV;
    private static final double PI2 = Math.PI * 2;
    private static final Vector2d[] CIRCLE_DATA;

    static {
        CIRCLE_DATA = new Vector2d[DIV];
        for (int i = 0; i < DIV; i++) {
            double rad = i / (double) DIV * PI2;
            CIRCLE_DATA[i] = new Vector2d(Math.sin(rad), Math.cos(rad));
        }
    }

    private static void calcDx(double ny, double tinput, double[] target) {
        double t = tinput * 0.1;
        target[0] = ImprovedNoise.noise(ny, t) * (0.3 + Math.pow(ny * 2, 1.4));
        target[1] = ImprovedNoise.noise(ny, t, 1) * (0.3 + Math.pow(ny * 2, 1.4));
    }

    private static double r(double ny, double t) {
        return (0.5 + 0.3 * ImprovedNoise.noise(ny, 0.2 * t)) + 0.5 * Math.pow(1.5 * ny, 2) + ImprovedNoise.noise(ny);
    }

    private static double rot(double ny, double t) {
        return 0.1 * (1 + 0.5 * ny) * t;
    }

    private static void drawRing(double y, double w, double[] vdx, double r, double rot) {
        double dx = vdx[0];
        double dz = vdx[1];

        for (int idx = 0; idx < DIV; idx++) {
            Vector2d v0 = CIRCLE_DATA[idx];
            Vector2d v1 = CIRCLE_DATA[(idx + 1) % DIV];

            double x0 = v0.x * r;
            double z0 = v0.y * r;
            double x1 = v1.x * r;
            double z1 = v1.y * r;

            double y0 = y + w / 2;
            double y1 = y - w / 2;
            double u0 = U_STEP * idx - rot;
            double u1 = u0 + U_STEP;

            GL11.glTexCoord2d(u0, 0);
            GL11.glNormal3d(x0, y0, 0);
            GL11.glVertex3d(x0 + dx, y0, z0 + dz);

            GL11.glTexCoord2d(u0, 1);
            GL11.glVertex3d(x0 + dx, y1, z0 + dz);

            GL11.glTexCoord2d(u1, 1);
            GL11.glVertex3d(x1 + dx, y1, z1 + dz);

            GL11.glTexCoord2d(u1, 0);
            GL11.glVertex3d(x1 + dx, y0, z1 + dz);
        }
    }

    public static void doRender(TornadoEffect eff) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderUtils.loadTexture(TEXTURE);
        GL11.glPushMatrix();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4d(1, 1, 1, eff.alpha * 0.7);
        GL11.glBegin(GL11.GL_QUADS);

        double[] vdx = new double[2];
        double time = eff.time();

        for (TornadoEffect.Ring ring : eff.getRings()) {
            double ny = ring.y / eff.ht;
            calcDx(ny, time, vdx);
            vdx[0] *= eff.sz * eff.dscale;
            vdx[1] *= eff.sz * eff.dscale;
            double vr = r(ny, time) * eff.sz * ring.sizeScale;
            drawRing(ring.y, ring.width, vdx, vr, rot(ny, time) + ring.phase);
        }

        GL11.glEnd();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}