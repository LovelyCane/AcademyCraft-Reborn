/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Registers the class as a listener into either FML or Forge bus.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegEventHandler {
}

class RegEventHandlerImpl {

    @StateEventCallback
    private static void preInit(FMLPreInitializationEvent ev) {
        ReflectionUtils.getFields(RegEventHandler.class).forEach(field -> {
            try {
                Object obj = field.get(null);
                Debug.assertNotNull(obj);
                MinecraftForge.EVENT_BUS.register(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
