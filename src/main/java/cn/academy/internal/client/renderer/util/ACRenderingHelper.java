package cn.academy.internal.client.renderer.util;

import cn.academy.Resources;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.HudUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

/**
 * Some drawing utils.
 *
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class ACRenderingHelper {
    public static ResourceLocation GLOW_L = glowtex("left"), GLOW_R = glowtex("right"), GLOW_U = glowtex("up"), GLOW_D = glowtex("down"), GLOW_RU = glowtex("ru"), GLOW_RD = glowtex("rd"), GLOW_LU = glowtex("lu"), GLOW_LD = glowtex("ld");

    public static void drawGlow(double x, double y, double width, double height, double size, Color glowColor) {
        Colors.bindToGL(glowColor);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        gdraw(GLOW_L, x - size, y, size, height);
        gdraw(GLOW_R, x + width, y, size, height);
        gdraw(GLOW_U, x, y - size, width, size);
        gdraw(GLOW_D, x, y + height, width, size);
        gdraw(GLOW_RU, x + width, y - size, size, size);
        gdraw(GLOW_RD, x + width, y + height, size, size);
        gdraw(GLOW_LU, x - size, y - size, size, size);
        gdraw(GLOW_LD, x - size, y + height, size, size);
    }

    public static boolean isThePlayer(EntityPlayer p) {
        return p.equals(Minecraft.getMinecraft().player);
    }

    public static double getHeightFix() {
        return 1.6;
    }

    private static void gdraw(ResourceLocation tex, double x, double y, double width, double height) {
        HudUtils.loadTexture(tex);
        HudUtils.rect(x, y, width, height);
    }

    private static ResourceLocation glowtex(String path) {
        return Resources.getTexture("guis/glow_" + path);
    }
}