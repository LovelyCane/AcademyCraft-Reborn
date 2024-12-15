package cn.lambdalib2.crafting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import cn.lambdalib2.LambdaLib2;
import cn.lambdalib2.util.Debug;

public class CustomMappingHelper {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RecipeName {
        String value();
    }

    public static void addMapping(RecipeRegistry reg, Class klass) {
        try {
            for (Field f : klass.getFields()) {
                if (f.isAnnotationPresent(RecipeName.class)) {
                    RecipeName anno = f.getAnnotation(RecipeName.class);
                    reg.map(anno.value(), f.get(null));
                }
            }
        } catch (Exception e) {
            Debug.error("An error occured analyzing recipe custom mapping");
            e.printStackTrace();
        }
    }

}
