package cn.lambdalib2.render.font;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

/**
 * A global registry for reusable font instances.
 */
public class Fonts {

    public static void register(String name, IFont font) {
        if (fonts.containsKey(name)) {
            throw new IllegalStateException("Font with name " + name + " already exists!");
        }
        fonts.put(name, font);
    }

    /**
     * @return An IFont associated with the name
     * @throws NullPointerException if no such font
     */
    public static IFont get(String name) {
        IFont result = fonts.get(name);
        Preconditions.checkNotNull(result);
        return result;
    }

    public static String getName(IFont font) {
        if (fonts.containsValue(font)) {
            return fonts.inverse().get(font);
        }
        throw new IllegalArgumentException("Font not registered");
    }

    public static IFont getOrDefault(String name) {
        if (exists(name)) {
            return get(name);
        } else {
            return getDefault();
        }
    }

    public static Collection<IFont> getFonts() {
        return ImmutableSet.copyOf(fonts.values());
    }

    public static IFont getDefault() {
        return DefaultFont;
    }

    public static boolean exists(String name) {
        return fonts.containsKey(name);
    }

    public static boolean isRegistered(IFont font) {
        return fonts.containsValue(font);
    }

    private static final IFont DefaultFont = TrueTypeFont.defaultFont;

    private static BiMap<String, IFont> fonts = HashBiMap.create();

    static {
        register("default", DefaultFont);
    }

    private Fonts() {}

}
