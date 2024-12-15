package cn.lambdalib2.cgui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark that a {@link cn.lambdalib2.cgui.component.Component} subtype should appear in CGui Editor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CGuiEditorComponent {
}
