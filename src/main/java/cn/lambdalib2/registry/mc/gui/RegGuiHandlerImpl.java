package cn.lambdalib2.registry.mc.gui;

import cn.academy.AcademyCraft;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RegGuiHandlerImpl {
    private static final Map<Object, ModGuiHandler> modHandlers = new HashMap<>();

    private static void addHandler(Object mod, GuiHandlerBase handler, int hash) {
        ModGuiHandler modHandler = modHandlers.get(mod);
        if (modHandler == null) {
            modHandler = new ModGuiHandler(mod);
            modHandlers.put(mod, modHandler);
            NetworkRegistry.INSTANCE.registerGuiHandler(mod, modHandler);
        }

        Debug.assert2(!modHandler.subHandlers.containsKey(hash), "Duplicate hash!!!");
        modHandler.subHandlers.put(hash, handler);
    }

    public static void init() {
        ReflectionUtils.getRawObjects(RegGuiHandler.class.getCanonicalName()).forEach(it -> {
            try {
                Class<?> clz = Class.forName(it.getClassName(), true, Loader.instance().getModClassLoader());
                Object mod = AcademyCraft.instance;
                Field field = clz.getDeclaredField(it.getObjectName());
                GuiHandlerBase handlerBase = (GuiHandlerBase) field.get(null);
                int hash = (field.getDeclaringClass().getCanonicalName() + "#" + field.getName()).hashCode();

                addHandler(mod, handlerBase, hash);
                handlerBase.init(mod, hash);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class ModGuiHandler implements IGuiHandler {
        public final Object mod;

        // Path hash -> handler
        private final HashMap<Integer, GuiHandlerBase> subHandlers = new HashMap<>();

        private ModGuiHandler(Object mod) {
            this.mod = mod;
        }

        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (!subHandlers.containsKey(ID)) {
                Debug.error("Invalid GUI id: " + ID);
                return null;
            }
            return subHandlers.get(ID).getServerContainer(player, world, x, y, z);
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (!subHandlers.containsKey(ID)) {
                Debug.error("Invalid GUI id: " + ID);
                return null;
            }
            return subHandlers.get(ID).getClientContainer(player, world, x, y, z);
        }
    }
}
