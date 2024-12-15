package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegEntityRenderFactory {
    public Class<? extends Entity> value();
}

class RegEntityRenderFactoryImpl {

    @SideOnly(Side.CLIENT)
    @StateEventCallback
    @SuppressWarnings("unchecked")
    private static void init(FMLInitializationEvent ev) {
        ReflectionUtils.getFields(RegEntityRenderFactory.class).forEach(field -> {
            try {
                IRenderFactory<?> instance = (IRenderFactory<?>) field.get(null);
                RegEntityRenderFactory anno = field.getAnnotation(RegEntityRenderFactory.class);
                RenderingRegistry.registerEntityRenderingHandler((Class) anno.getClass(), (IRenderFactory) instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
