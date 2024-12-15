package cn.lambdalib2.cgui.annotation;

import cn.lambdalib2.cgui.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a public field in Component is annotated with {@link CopyIgnore}, it will not get copied by
 * {@link Component#copy()} method even if it could.
 * @author WeAthFolD
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopyIgnore {

}
