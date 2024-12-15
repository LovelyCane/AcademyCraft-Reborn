/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.s11n;

import cn.lambdalib2.render.font.IFont;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Value-copy objects based on the rule of SerializationHelper.
 */
public class CopyHelper {

    private static final SerializationHelper serHelper = new SerializationHelper();

    public static final CopyHelper instance = new CopyHelper();

    @FunctionalInterface
    private interface ICopyFactory<T> {
        T copy(T origin);
    }

    private Map<Class<?>, ICopyFactory<?>> primitiveHandlers = new HashMap<>();

    /**
     * Directly returns the same reference (or box/unbox for primitive type), safe because they are immutable.
     */
    private <T> void direct(Class<T> type) {
        primitiveHandlers.put(type, (T value) -> value);
    }

    {
        direct(char.class);
        direct(Character.class);
        direct(float.class);
        direct(Float.class);
        direct(double.class);
        direct(Double.class);
        direct(int.class);
        direct(Integer.class);
        direct(String.class);
        direct(boolean.class);
        direct(Boolean.class);
        direct(ResourceLocation.class);
        direct(IFont.class);
        direct(Enum.class);
        primitiveHandlers.put(Color.class, o -> {
            Color c = (Color) o;
            Color ret = new Color();
            ret.setColor(c);
            return ret;
        });
        serHelper.regS11nType(Color.class);
    }

    /**
     * Value-copies the given object.
     * @throws RuntimeException if object creation or copy failed
     */
    public <T> T copy(T object) {
        if (isDirect(object)) {
            return copyDirect(object);
        }

        try {
            T ret = (T) object.getClass().newInstance();

            for (Field f : serHelper.getExposedFields(object.getClass())) {
                f.set(ret, copy(f.get(object)));
            }

            return ret;
        } catch (Exception e) {
            throw new RuntimeException("Error occured while copying object " + object, e);
        }
    }

    /**
     * @return If the object's copying is directly supported (No recursive value copying)
     */
    public boolean isDirect(Object obj) {
        if (obj == null) return true;
        return copyFactory(obj) != null;
    }

    private <T> ICopyFactory<T> copyFactory(T obj) {
        for (Entry<Class<?>, ICopyFactory<?>> ent : primitiveHandlers.entrySet()) {
            if (ent.getKey().isInstance(obj)) {
                return (ICopyFactory) ent.getValue();
            }
        }
        return null;
    }

    /**
     * @return The copy of a directly supported object.
     * @throws RuntimeException if the object is not directly supported.
     */
    public <T> T copyDirect(T object) {
        if (object == null) return null;

        ICopyFactory<T> factory = copyFactory(object);
        if (factory != null) {
            return factory.copy(object);
        }

        throw new RuntimeException("Can't find handler for primitive " + object + " of type " + object.getClass());
    }

}
