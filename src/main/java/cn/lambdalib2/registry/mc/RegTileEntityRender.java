package cn.lambdalib2.registry.mc;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegTileEntityRender {
    public Class<? extends TileEntity> value();
}

class RegTileEntityRenderImpl {
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @StateEventCallback
    private static void init(FMLInitializationEvent ev) {
        List<Field> fields = ReflectionUtils.getFields(RegTileEntityRender.class);
        fields.forEach(field -> {
            try {
                Debug.require(Modifier.isStatic(field.getModifiers()));
                TileEntitySpecialRenderer r = (TileEntitySpecialRenderer) field.get(null);
                RegTileEntityRender anno = field.getAnnotation(RegTileEntityRender.class);
                ClientRegistry.bindTileEntitySpecialRenderer((Class) anno.value(), r);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Debug.log("RegTileEntityRender: " + (fields.size()) + " renders registered.");
    }
}
