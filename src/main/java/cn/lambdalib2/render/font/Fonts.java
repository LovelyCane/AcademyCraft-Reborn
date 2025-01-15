package cn.lambdalib2.render.font;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A global registry for reusable font instances.
 */
public class Fonts {
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

    public static IFont getDefault() {
        return DefaultFont;
    }

    public static boolean exists(String name) {
        return fonts.containsKey(name);
    }

    private static final IFont DefaultFont = TrueTypeFont.defaultFont;

    private static final BiMap<String, IFont> fonts = HashBiMap.create();

    static {
        fonts.put("default", DefaultFont);
    }

    private Fonts() {}
}
