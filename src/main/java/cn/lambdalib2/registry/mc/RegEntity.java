package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.RegistryContext;
import cn.lambdalib2.registry.RegistryMod;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegEntity {

    int trackRange() default 32;
    int freq() default 3;
    boolean updateVelocity() default true;

}

class RegEntityImpl {

    private static Map<Object, Integer> counterMap = new HashMap<>();

    @StateEventCallback
    @SuppressWarnings("unchecked")
    private static void init(FMLInitializationEvent ev) {
        List<Class<?>> types = ReflectionUtils.getClasses(RegEntity.class);
        Map<Object, ModContainer> map = Loader.instance().getReversedModObjectList();
        types.sort(Comparator.comparing(Class::getCanonicalName)); // Sort by entity name, ensuring order
        types.forEach(type -> {
            if (type.getAnnotation(SideOnly.class) != null) {
                Debug.error("SideOnly entity " + type + " need not to be registered");
                return;
            }
            RegEntity anno = type.getAnnotation(RegEntity.class);
            Object mod = RegistryContext.getModForPackage(type.getCanonicalName());
            String domain = mod.getClass().getAnnotation(RegistryMod.class).resourceDomain();

            Loader.instance().setActiveModContainer(Debug.assertNotNull(map.get(mod)));

            int count = counterMap.getOrDefault(mod, 0);
            EntityRegistry.registerModEntity(
                new ResourceLocation(domain, type.getSimpleName()),
                (Class<? extends Entity>) type,
                type.getSimpleName(),
                count,
                mod,
                anno.trackRange(),
                anno.freq(),
                anno.updateVelocity()
            );
            counterMap.put(mod, count + 1);
        });
    }

}