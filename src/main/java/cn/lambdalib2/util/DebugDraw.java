package cn.lambdalib2.util;

import cn.lambdalib2.render.Mesh;
import cn.lambdalib2.render.RenderMaterial;
import cn.lambdalib2.render.ShaderScript;
import cn.lambdalib2.render.TransformUtils;
import cn.lambdalib2.render.mc.RenderAllEntityEvent;
import cn.lambdalib2.render.mc.RenderEventDispatch;
import cn.lambdalib2.render.primitive.SphereMeshFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class DebugDraw {

    private static class SphereDraw {
        Matrix4f mat;
        Vector4f color;
        Vector3f pos;
        float radius;
    }

    private static final Matrix4f transformMat = new Matrix4f();

    private static final List<SphereDraw> sphereDraws = new ArrayList<>();

    private static final Vector4f color = new Vector4f();

    private static final Mesh sphereMesh = SphereMeshFactory.create(1.0f, 20);

    private static ShaderScript shader;

    private static boolean init = false;

    public static class DebugDrawEvent extends Event {}

    public static void setTransform(Matrix4f matrix) {
        transformMat.load(matrix);
    }

    public static Matrix4f getTransform() {
        return transformMat;
    }

    public static void setColor(Vector4f color) {
        DebugDraw.color.set(color);
    }

    public static Vector4f getColor() {
        return color;
    }

    public static void sphere(Vector3f center, float radius) {
        SphereDraw draw = new SphereDraw();
        draw.color = new Vector4f(color);
        draw.pos = new Vector3f(center);
        draw.radius = radius;
        draw.mat = new Matrix4f(transformMat);
        sphereDraws.add(draw);
    }

    @SubscribeEvent
    public void postRenderEntity(RenderAllEntityEvent event) {
        if (event.pass != 1)
            return;

        checkInit();

        MinecraftForge.EVENT_BUS.post(new DebugDrawEvent());

        renderSpheres();
    }

    private static void checkInit() {
        if (!init) {
            init = true;

            shader = ShaderScript.loadFromResource("/assets/lambdalib2/shader/DebugDraw.glsl");
        }
    }

    private static void renderSpheres() {
        for (SphereDraw draw : sphereDraws) {
            RenderMaterial material = new RenderMaterial(shader);

            Matrix4f mat = TransformUtils.scale(draw.radius, draw.radius, draw.radius);
            Matrix4f.mul(mat, TransformUtils.translate(draw.pos.x, draw.pos.y, draw.pos.z), mat);
            Matrix4f.mul(draw.mat, mat, mat);

            material.setMat4("uMVP", mat);
            material.setVec4("uColor", new Vector4f(draw.color));

            // T = draw.T * Offset(P) * Scale(R)
            RenderEventDispatch.entityPass.draw(material, sphereMesh);
        }

        sphereDraws.clear();
    }

}
