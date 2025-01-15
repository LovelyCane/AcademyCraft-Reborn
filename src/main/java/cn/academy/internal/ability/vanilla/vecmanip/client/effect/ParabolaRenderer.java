package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.Resources;
import cn.academy.internal.ability.vanilla.vecmanip.skill.VecAccelContext;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.render.legacy.ShaderSimple;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RenderUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
@RegEntityRender(ParabolaEffect.class)
@SuppressWarnings("unused")
public class ParabolaRenderer extends Render<ParabolaEffect> {
    private final ResourceLocation texture = Resources.getTexture("effects/glow_line");
    private final ArrayList<Vec3d> vertices = new ArrayList<>();

    public ParabolaRenderer(RenderManager m) {
        super(m);
    }

    @Override
    public void doRender(ParabolaEffect entity, double x, double y, double z, float partialTicks, float wtf) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            VecAccelContext ctx = entity.ctx;
            Vec3d speed = ctx.initSpeed(partialTicks);
            EntityPlayer player = ctx.player;

            double yawLerp = MathUtils.lerpf(player.prevRotationYaw, player.rotationYaw, partialTicks);
            double pitchLerp = MathUtils.lerpf(player.prevRotationPitch, player.rotationPitch, partialTicks);

            Vec3d lookFix = VecUtils.toDirVector((float) yawLerp, (float) pitchLerp);
            Vec3d lookRot = VecUtils.multiply(new Vec3d(lookFix.x, 0, lookFix.z).rotateYaw(90), -0.08);
            lookRot = new Vec3d(lookRot.x, 1.56, lookRot.z);

            Vec3d pos = VecUtils.subtract(lookRot, VecUtils.multiply(lookFix, 0.12));

            vertices.clear();

            double dt = 0.02;
            for (int i = 0; i < 100; i++) {
                vertices.add(pos);
                speed = VecUtils.multiply(speed, 0.98);
                pos = pos.add(VecUtils.multiply(speed, dt));
                speed = new Vec3d(speed.x, speed.y - dt * 1.9, speed.z);
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderUtils.loadTexture(texture);
            ShaderSimple.instance().useProgram();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glColor4f(1, 1, 1, 0.6f);

            for (int idx = 1; idx < vertices.size(); idx++) {
                double h = 0.02;
                Vec3d prev = vertices.get(idx - 1);
                Vec3d cur = vertices.get(idx);

                float alpha = 0.7f * (1 - idx * 0.03f);
                if (entity.canPerform) {
                    GL11.glColor4f(1, 1, 1, alpha);
                } else {
                    GL11.glColor4f(1, 0.2f, 0.2f, alpha);
                }

                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2d(0, 0);
                GL11.glVertex3d(prev.x, prev.y + h, prev.z);
                GL11.glTexCoord2d(0, 1);
                GL11.glVertex3d(prev.x, prev.y - h, prev.z);
                GL11.glTexCoord2d(1, 1);
                GL11.glVertex3d(cur.x, cur.y - h, cur.z);
                GL11.glTexCoord2d(1, 0);
                GL11.glVertex3d(cur.x, cur.y + h, cur.z);
                GL11.glEnd();
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL20.glUseProgram(0);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(ParabolaEffect entity) {
        return null;
    }
}