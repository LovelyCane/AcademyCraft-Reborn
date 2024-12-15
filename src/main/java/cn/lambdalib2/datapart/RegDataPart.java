package cn.lambdalib2.datapart;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.EnumSet;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegDataPart {

    /**
     * @return The type that this DataPart applies on. Also applies for all subclasses.
     */
    Class<? extends Entity> value();

    /**
     * @return At what sides this DataPart should be constructed
     */
    Side[] side() default { Side.CLIENT, Side.SERVER };

}

class RegDataPartImpl {

    @SuppressWarnings("unchecked")
    @StateEventCallback
    private static void init(FMLPreInitializationEvent ev) {
        ReflectionUtils.getClasses(RegDataPart.class).forEach(type -> {
            RegDataPart anno = type.getAnnotation(RegDataPart.class);
            Class<? extends Entity> regType = anno.value();
            EntityData.register(
                (Class) type,
                EnumSet.copyOf(Arrays.asList(anno.side())),
                regType::isAssignableFrom
            );
        });
        EntityData.bake();
    }

}
