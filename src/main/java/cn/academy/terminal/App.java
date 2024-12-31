package cn.academy.terminal;

import cn.academy.Resources;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public abstract class App {
    int appid;
    private final String name;
    protected ResourceLocation icon;

    private boolean preInstalled = false;

    public App(String _name) {
        name = _name;
        icon = getTexture("icon");
    }

    protected ResourceLocation getTexture(String textureName) {
        return Resources.getTexture("guis/apps/" + name + "/" + textureName);
    }

    private String localKey(String key) {
        return "ac.app." + name + "." + key;
    }

    protected String local(String key) {
        return I18n.format(localKey(key));
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getIcon() {
        return icon;
    }

    public void setPreInstalled() {
        preInstalled = true;
    }

    public int getID() {
        return appid;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return local("name");
    }

    public String getDisplayKey() {
        return localKey("name");
    }

    public final boolean isPreInstalled() {
        return preInstalled;
    }

    @SideOnly(Side.CLIENT)
    public abstract AppEnvironment createEnvironment();
}