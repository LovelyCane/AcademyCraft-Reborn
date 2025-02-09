package cn.academy.internal.ability.context;

import cn.lambdalib2.util.ReflectionUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;

@SideOnly(Side.CLIENT)
public class RegClientContextImpl {
    @SideOnly(Side.CLIENT)
    public static void init() {
        ReflectionUtils.getClasses(RegClientContext.class).forEach(type -> {
            RegClientContext anno = type.getAnnotation(RegClientContext.class);
            for (Constructor ctor : type.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 1 && Context.class.isAssignableFrom(ctor.getParameterTypes()[0])) {
                    ctor.setAccessible(true);
                    ClientContext.clientTypes.put(anno.value(), ctx -> {
                        try {
                            return (ClientContext) ctor.newInstance(ctx);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    return;
                }
            }

            throw new IllegalArgumentException("No appropriate constructor found for " + type);
        });
    }
}