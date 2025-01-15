package cn.lambdalib2;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class CorePlugin implements IFMLLoadingPlugin {
    private static boolean deobfEnabled;

    public static boolean isDeobfEnabled() {
        return deobfEnabled;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"cn.lambdalib2.registry.impl.RegistryTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "cn.lambdalib2.ModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        deobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
