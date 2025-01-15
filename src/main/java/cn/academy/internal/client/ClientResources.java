package cn.academy.internal.client;

import cn.academy.Resources;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.render.font.TrueTypeFont;
import cn.lambdalib2.render.obj.ObjLegacyRender;
import cn.lambdalib2.render.obj.ObjParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A delegation for client resources loading. Should not refer to explicitly.
 *
 * @see Resources
 */
@SideOnly(Side.CLIENT)
public class ClientResources {
    private static final TrueTypeFont font;
    private static final TrueTypeFont fontBold;

    private static final Map<ResourceLocation, ObjLegacyRender> cachedModels = new HashMap<>();

    public static IFont font() {
        return font;
    }

    public static IFont fontBold() {
        return fontBold;
    }

    public static ResourceLocation preloadMipmapTexture(String loc) {
        TextureManager texManager = Minecraft.getMinecraft().getTextureManager();

        ResourceLocation ret = Resources.getTexture(loc);

        ITextureObject loadedTexture = texManager.getTexture(ret);

        return ret;
    }

    public static ResourceLocation preloadTexture(String loc) {
        ResourceLocation ret = Resources.getTexture(loc);

        TextureManager texManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject loadedTexture = texManager.getTexture(ret);

        return ret;
    }

    public static TextBox newTextBox() {
        TextBox ret = new TextBox();
        ret.font = font();
        return ret;
    }

    public static TextBox newTextBox(IFont.FontOption option) {
        TextBox ret = new TextBox(option);
        ret.font = font();
        return ret;
    }

    public static ObjLegacyRender getModel(String mdlName) {
        return cachedModels.computeIfAbsent(new ResourceLocation("academy", "models/" + mdlName + ".obj"), (loc) -> new ObjLegacyRender(ObjParser.parse(loc)));
    }

    static {
        font = TrueTypeFont.defaultFont;
        fontBold = new TrueTypeFont(font.font.deriveFont(Font.BOLD));
    }
}