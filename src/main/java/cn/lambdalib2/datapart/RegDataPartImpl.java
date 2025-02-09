package cn.lambdalib2.datapart;

import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.entity.Entity;

import java.util.Arrays;
import java.util.EnumSet;

public class RegDataPartImpl {
    public static void init( ) {
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
