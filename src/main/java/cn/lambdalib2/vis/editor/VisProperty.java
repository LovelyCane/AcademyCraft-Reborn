package cn.lambdalib2.vis.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supply extra information for {@link ObjectInspection}. You can also use
 *  this annotation to force inspection of private values.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisProperty {

    /**
     * @return The name of this property displayed in the editor tab. If not supplied,
     *     will use the field name.
     */
    String name() default "";

}
