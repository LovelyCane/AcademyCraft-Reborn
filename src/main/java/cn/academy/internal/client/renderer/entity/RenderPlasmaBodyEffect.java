package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.ability.vanilla.vecmanip.client.effect.PlasmaBodyEffect;
import cn.academy.internal.client.CameraPosition;
import cn.lambdalib2.render.legacy.GLSLMesh;
import cn.lambdalib2.render.legacy.LegacyMeshUtils;
import cn.lambdalib2.render.legacy.LegacyShaderProgram;
import cn.lambdalib2.util.EntityLook;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

@SideOnly(Side.CLIENT)
public class RenderPlasmaBodyEffect extends Render<PlasmaBodyEffect> {
    private final GLSLMesh mesh;
    private final LegacyShaderProgram shader;
    private final int pos_ballCount;
    private final int pos_balls;
    private final int pos_alpha;

    public RenderPlasmaBodyEffect(RenderManager m) {
        super(m);
        this.mesh = LegacyMeshUtils.createBillboard(new GLSLMesh(), -0.5, -0.5, 0.5, 0.5);
        this.shader = new LegacyShaderProgram();

        shader.linkShader(new ResourceLocation("academy:shaders/plasma_body.vert"), GL_VERTEX_SHADER);
        shader.linkShader(new ResourceLocation("academy:shaders/plasma_body.frag"), GL_FRAGMENT_SHADER);
        shader.compile();

        this.pos_ballCount = shader.getUniformLocation("ballCount");
        this.pos_balls = shader.getUniformLocation("balls");
        this.pos_alpha = shader.getUniformLocation("alpha");
    }

    private void setupOpenGLState() {
        glDepthMask(false);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glUseProgram(shader.getProgramID());
    }

    private void restoreOpenGLState() {
        glUseProgram(0);
        glEnable(GL_ALPHA_TEST);
        glDepthMask(true);
    }

    @Override
    public void doRender(PlasmaBodyEffect eff, double x, double y, double z, float partialTicks, float wtf) {
        float size = 22f;

        Vector3f playerPos = new Vector3f((float) renderManager.viewerPosX, (float) renderManager.viewerPosY, (float) renderManager.viewerPosZ);

        Matrix4f matrix = new Matrix4f();
        acquireMatrix(GL_MODELVIEW_MATRIX, matrix);

        // Update OpenGL state only once
        setupOpenGLState();

        // Update balls position
        float deltaTime = (float) eff.deltaTime();
        eff.updateAlpha();
        float alpha = (float) Math.pow(eff.alpha, 2);

        updateBalls(eff, deltaTime, playerPos, matrix);

        glUniform1f(pos_alpha, alpha);

        // Camera position and rendering transformation
        Vec3d campos = CameraPosition.getVec3d();
        Vec3d delta = new Vec3d(x, y, z).subtract(campos);
        EntityLook yp = new EntityLook(delta);

        glPushMatrix();
        glTranslated(x, y, z);
        glRotated(-yp.yaw + 180, 0, 1, 0);
        glRotated(-yp.pitch, 1, 0, 0);
        glScaled(size, size, 1);

        mesh.draw(shader.getProgramID());

        glPopMatrix();

        restoreOpenGLState();
    }

    private void updateBalls(PlasmaBodyEffect eff, float deltaTime, Vector3f playerPos, Matrix4f matrix) {
        glUniform1i(pos_ballCount, eff.balls.size());

        for (int idx = 0; idx < eff.balls.size(); idx++) {
            PlasmaBodyEffect.BallInst ball = eff.balls.get(idx);
            float hrphase = ball.hmove.phase(deltaTime);
            float vtphase = ball.vmove.phase(deltaTime);

            float dx = ball.hmove.amp * MathHelper.sin(hrphase);
            float dy = ball.vmove.amp * MathHelper.sin(vtphase);
            float dz = ball.hmove.amp * MathHelper.cos(hrphase);

            Vector4f pos = new Vector4f((float) (eff.posX + ball.center.x + dx - playerPos.x), (float) (eff.posY + ball.center.y + dy - playerPos.y), (float) (eff.posZ + ball.center.z + dz - playerPos.z), 1);

            Vector4f camPos = Matrix4f.transform(matrix, pos, null);
            glUniform4f(pos_balls + idx, camPos.x, camPos.y, -camPos.z, ball.size);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(PlasmaBodyEffect entity) {
        return null;
    }

    private void acquireMatrix(int matrixType, Matrix4f dst) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        glGetFloat(matrixType, buffer);
        dst.load(buffer);
    }
}
