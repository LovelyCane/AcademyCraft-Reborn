/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.s11n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controls serialization strategy of this class.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeStrategy {
    enum ExposeStrategy {
        /** Just public fields */
        PUBLIC,
        /** Includes private fields */
        ALL
    }

    /**
     * @return How fields of this recursive type are exposed.
     */
    ExposeStrategy strategy() default ExposeStrategy.PUBLIC;

    /**
     * @return Should we serialize all the fields exposed in the class, not just serializable ones. Might be useful
     *  in some contexts.
     */
    boolean all() default false;
}
