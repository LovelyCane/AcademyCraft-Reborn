package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface RegCommand {
}

class RegCommandImpl {
    @StateEventCallback
    private static void serverStart(FMLServerStartingEvent ev) {
        ReflectionUtils.getClasses(RegCommand.class).forEach(type -> {
            Debug.require(ICommand.class.isAssignableFrom(type), "Class must implement ICommand");
            try {
                Constructor<ICommand> ctor = (Constructor<ICommand>) type.getDeclaredConstructor();
                ICommand instance = ctor.newInstance();
                ev.registerServerCommand(instance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        ReflectionUtils.getFields(RegCommand.class).forEach(field -> {
            Debug.require(Modifier.isStatic(field.getModifiers()), "Field must be static");
            Object obj = null;
            try {
                obj = field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Debug.require(obj != null && ICommand.class.isInstance(obj), "Object must be non-null and is ICommand");
            ev.registerServerCommand((ICommand) obj);
        });
    }
}