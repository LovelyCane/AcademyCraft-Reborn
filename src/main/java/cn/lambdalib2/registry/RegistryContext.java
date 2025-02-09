package cn.lambdalib2.registry;

import cn.lambdalib2.registry.impl.RegistryManager;
import cn.lambdalib2.registry.impl.RegistryManager.ModContext;
import cn.lambdalib2.util.Debug;

/**
 * Provides contextual information during startup registry.
 */
public class RegistryContext {
    public static Object getModForPackage(String classPath)  {
        ModContext ctx = Debug.assertNotNull(RegistryManager.INSTANCE.findMod(classPath),
            "No mod handling class " + classPath + " found.");
        return Debug.assertNotNull(ctx.modObject, "Mod hasn't be constructed! Use this method only AFTER preInit stage.");
    }
}
