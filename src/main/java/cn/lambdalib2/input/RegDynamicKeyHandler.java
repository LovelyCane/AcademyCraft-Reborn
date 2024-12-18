package cn.lambdalib2.input;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegDynamicKeyHandler {
    int keyID();
}

class RegDynKeyHandlerImpl {
    @StateEventCallback
    @SideOnly(Side.CLIENT)
    private static void init(FMLInitializationEvent ev) {
        ReflectionUtils.getFields(RegDynamicKeyHandler.class).forEach(field -> {
            field.setAccessible(true);
            RegDynamicKeyHandler anno = field.getAnnotation(RegDynamicKeyHandler.class);
            try {
                KeyManager.dynamic.addKeyHandler(anno.keyID(), (KeyHandler) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
