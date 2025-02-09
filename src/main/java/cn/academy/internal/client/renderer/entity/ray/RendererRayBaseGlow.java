package cn.academy.internal.client.renderer.entity.ray;

import cn.academy.internal.entity.IRay;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RenderUtils;
import cn.lambdalib2.util.VecUtils;
import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

/**
 * Renderer to draw glow texture
 *
 * @author WeAthFolD
 */
@SuppressWarnings({"unchecked"})
public abstract class RendererRayBaseGlow<T extends IRay> extends Render<Entity> {
    {
        this.shadowOpaque = 0;
    }

    public RendererRayBaseGlow(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(@Nonnull Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        T ray = (T) entity;

        glPushMatrix();

        doTransform();

        Vec3d position = ray.getRayPosition();
        Vec3d relativePosition = VecUtils.subtract(position, new Vec3d(x, y, y));
        glTranslated(x, y, z);

        //Calculate the most appropriate 'billboard-up' direction.
        //The ray viewing direction.
        Vec3d dir = VecUtils.toDirVector(entity, partialTicks);
        //Pick two far enough start and end point.
        Vec3d start = VecUtils.multiply(dir, ray.getStartFix()), end = VecUtils.add(start, VecUtils.multiply(dir, ray.getLength() - ray.getStartFix()));

        Vec3d upDir;
        boolean firstPerson = ViewOptimize.isFirstPerson(ray);
        if (firstPerson) {
            upDir = new Vec3d(0, 1, -0.5);
        } else {
            //Get closest point for view judging.
            Vec3d pt = new Vec3d(0, 0, 0);

            //The player viewing direction towards pt.
            Vec3d prepViewDir = VecUtils.add(pt, relativePosition);

            // cross product to get the 'up' vector
            upDir = VecUtils.crossProduct(prepViewDir, dir);
        }

        upDir = upDir.normalize();

        if (ray.needsViewOptimize()) {
            yaw = MathUtils.lerpDegree(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
            Vec3d vec = ViewOptimize.getFixVector(ray);
            vec = vec.rotateYaw((float) ((270 - yaw) / 180 * Math.PI));
            start = VecUtils.add(start, vec);
        }

        doTransform();

        //Now delegate to the render itself~
        draw(ray, start, end, upDir);

        glPopMatrix();
    }

    protected void doTransform() {
    }

    protected void drawBoard(Vec3d start, Vec3d end, Vec3d upDir, double width) {
        width /= 2;
        Vec3d v1 = VecUtils.add(start, VecUtils.multiply(upDir, width)), v2 = VecUtils.add(start, VecUtils.multiply(upDir, -width)), v3 = VecUtils.add(end, VecUtils.multiply(upDir, -width)), v4 = VecUtils.add(end, VecUtils.multiply(upDir, width));

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        glBegin(GL_QUADS);
        RenderUtils.addVertexLegacy(v1, 0, 1);
        RenderUtils.addVertexLegacy(v2, 0, 0);
        RenderUtils.addVertexLegacy(v3, 1, 0);
        RenderUtils.addVertexLegacy(v4, 1, 1);
        glEnd();
    }

    /**
     * Draw the ray at the origin. The ray's heading direction should be toward x+,
     * and normal is always in z direction.
     *
     * @param start   The start point
     * @param end     The end point
     * @param sideDir the suggested billboard-up direction. You can ignore this if not drawing a billboard.
     */
    protected abstract void draw(T ray, Vec3d start, Vec3d end, Vec3d sideDir);

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull Entity entity) {
        return null;
    }
}