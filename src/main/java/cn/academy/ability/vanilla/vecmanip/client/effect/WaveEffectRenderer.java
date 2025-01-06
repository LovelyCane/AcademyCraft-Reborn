package cn.academy.ability.vanilla.vecmanip.client.effect;

import cn.academy.Resources;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.render.legacy.LegacyMesh;
import cn.lambdalib2.render.legacy.LegacyMeshUtils;
import cn.lambdalib2.render.legacy.SimpleMaterial;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.vis.curve.CubicCurve;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@RegEntityRender(WaveEffect.class)
@SuppressWarnings("unused")
public class WaveEffectRenderer extends Render<WaveEffect> {
    private final CubicCurve alphaCurve = new CubicCurve();
    private final ResourceLocation texture = Resources.getTexture("effects/glow_circle");
    private final LegacyMesh mesh = new LegacyMesh();
    private final SimpleMaterial material = new SimpleMaterial(texture).setIgnoreLight();
    private final CubicCurve sizeCurve = new CubicCurve();

    public WaveEffectRenderer(RenderManager m) {
        super(m);
        alphaCurve.addPoint(0, 0);
        alphaCurve.addPoint(0.2, 1);
        alphaCurve.addPoint(0.5, 1);
        alphaCurve.addPoint(0.8, 1);
        alphaCurve.addPoint(1, 0);

        LegacyMeshUtils.createBillboard(mesh, -0.5, -0.5, 1, 1);

        sizeCurve.addPoint(0, 0.4);
        sizeCurve.addPoint(0.2, 0.8);
        sizeCurve.addPoint(2.5, 1.5);
    }

    @Override
    public void doRender(WaveEffect effect, double x, double y, double z, float v3, float v4) {
        double maxAlpha = MathUtils.clampd(0, 1, alphaCurve.valueAt(effect.ticksExisted / (double) effect.life));

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotated(-effect.rotationYaw, 0, 1, 0);
        GL11.glRotated(effect.rotationPitch, 1, 0, 0);

        double zOffset = effect.ticksExisted / 40.0;
        GL11.glTranslated(0, 0, zOffset);

        for (WaveEffect.Ring ring : effect.ringList) {
            double alpha = MathUtils.clampd(0, 1, alphaCurve.valueAt((effect.ticksExisted - ring.timeOffset) / (double) ring.life));
            float realAlpha = Math.min((float) maxAlpha, (float) alpha);

            if (realAlpha > 0) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0, ring.offset);

                double sizeScale = sizeCurve.valueAt(MathUtils.clampd(0, 1.62, effect.ticksExisted / 20.0));
                GL11.glScaled(ring.size * sizeScale, ring.size * sizeScale, 1);
                material.color.setAlpha(Colors.f2i(realAlpha * 0.7f));
                mesh.draw(material);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public ResourceLocation getEntityTexture(WaveEffect entity) {
        return null;
    }
}