package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Registers an entity render class.
 * The class must have a constructor that accepts RenderManager as its only argument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegEntityRender {
    public Class<? extends Entity> value();
}

class RegEntityRenderImpl {

    @SideOnly(Side.CLIENT)
    @StateEventCallback
    @SuppressWarnings("unchecked")
    private static void preInit(FMLPreInitializationEvent ev) {
        ReflectionUtils.getClasses(RegEntityRender.class).forEach(type -> {
            try {
                RegEntityRender anno = type.getAnnotation(RegEntityRender.class);
                Constructor<Render<?>> ctor = (Constructor) type.getConstructor(RenderManager.class);
                RenderingRegistry.registerEntityRenderingHandler(anno.value(), manager -> {
                    try {
                        return (Render) ctor.newInstance(manager);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
