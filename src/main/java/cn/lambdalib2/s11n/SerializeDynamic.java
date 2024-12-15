package cn.lambdalib2.s11n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To optimize data usage, type info about the recursive object's fields are emitted. That will result in
 *  serialization with type defined in the class, rather than its runtime type. If you want normal dynamic type
 *  behaviour to be present, annotate this on the field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeDynamic {}
