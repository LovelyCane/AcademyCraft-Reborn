package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.ability.vanilla.electromaster.skill.HandlerEntity;
import cn.academy.internal.ability.vanilla.electromaster.skill.MineElem;
import cn.lambdalib2.render.legacy.*;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public class RenderHandlerEntity extends Render<HandlerEntity> {
    private final ResourceLocation texture = Resources.getTexture("effects/mineview");
    private final LegacyMesh mesh = LegacyMeshUtils.createBoxWithUV(null, 0.05, 0.05, 0.05, 0.9, 0.9, 0.9);
    private final SimpleMaterial material = new SimpleMaterial(texture).setIgnoreLight();

    private final Color[] colors = new Color[]{new Color(115, 200, 227, 0), // default color
            new Color(161, 181, 188, 0), // harvest level 0-3
            new Color(87, 231, 248, 0), new Color(97, 204, 94, 0), new Color(235, 109, 84, 0)};

    public RenderHandlerEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(HandlerEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Tessellator t = Tessellator.instance;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_FOG);

        RenderUtils.loadTexture(texture);

        material.onRenderStage(RenderStage.BEFORE_TESSELLATE);

        t.startDrawing(GL11.GL_TRIANGLES);
        for (MineElem me : entity.aliveSims) {
            t.setTranslation(me.x - renderManager.viewerPosX, me.y - renderManager.viewerPosY, me.z - renderManager.viewerPosZ);
            drawSingle(me, calcAlpha(entity.posX - me.x, entity.posY - me.y, entity.posZ - me.z, entity.range));
        }
        t.draw();

        GL11.glEnable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private int calcAlpha(double x, double y, double z, double range) {
        double jdg = 1 - MathUtils.length(x, y, z) / range * 2.2;
        return Colors.f2i(0.3f + (float) (jdg * 0.7));
    }

    private void drawSingle(MineElem me, int alpha) {
        Color color = colors[Math.min(colors.length - 1, me.level)];
        color.setAlpha(alpha);
        Colors.bindToGL(color);
        mesh.redrawWithinBatch(material);
    }

    @Override
    protected ResourceLocation getEntityTexture(HandlerEntity entity) {
        return null;
    }
}
