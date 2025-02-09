package cn.lambdalib2.datapart;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    Side[] side() default {Side.CLIENT, Side.SERVER};
}