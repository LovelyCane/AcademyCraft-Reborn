package cn.academy.client.render.entity;

import cn.academy.Resources;
import cn.academy.entity.EntityCoinThrowing;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author KSkun
 */

@RegEntityRender(EntityCoinThrowing.class)
public class RendererCoinThrowing extends Render<EntityCoinThrowing> {

    private final Minecraft mc = Minecraft.getMinecraft();

    public RendererCoinThrowing(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityCoinThrowing entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer player = entity.player;
        if (player == null || entity.posY < player.posY) return;

        boolean isFirstPerson = player == mc.player && mc.gameSettings.thirdPersonView == 0;

        double dt = (GameTimer.getTime() * 1000) % 150;

        if (entity.isSync && player == mc.player) return;

        GL11.glPushMatrix();
        {
            // Reset position if in first person view
            if (player == mc.player) {
                x = z = 0;
            }

            // Move the render position
            GL11.glTranslated(x, y, z);

            GL11.glRotated(isFirstPerson ? player.rotationYaw : player.renderYawOffset, 0, -1, 0);

            // Apply hand-based offsets
            GL11.glTranslated(entity.hand == EnumHand.MAIN_HAND ? -0.5 : 0.2, 0.85, 0.30);

            // Apply scale and rotation
            float scale = 0.3F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslated(0.5, 0.5, 0);
            GL11.glRotated((dt * 360.0 / 300.0), entity.axis.x, entity.axis.y, entity.axis.z);
            GL11.glTranslated(-0.5, -0.5, 0);

            // Draw the item
            RenderUtils.drawEquippedItem(0.0625, Resources.TEX_COIN_FRONT, Resources.TEX_COIN_BACK);
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCoinThrowing entity) {
        return null;
    }
}