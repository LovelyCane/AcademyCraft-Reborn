package cn.lambdalib2.util;

import net.minecraft.util.ResourceLocation;

import java.io.InputStream;

public class ResourceUtils {

    public static InputStream getResourceStream(ResourceLocation res) {
        try {
            return Debug.assertNotNull(
                ResourceUtils.class.getResourceAsStream(resToPath(res)), () -> "Can't find resource " + res.toString()
            );
        } catch(Exception e) {
            throw new RuntimeException("Invalid resource " + res, e);
        }
    }

    public static InputStream getResourceStreamNullable(ResourceLocation res) {
        return ResourceUtils.class.getResourceAsStream(resToPath(res));
    }

    private static String resToPath(ResourceLocation res) {
        String domain = res.getNamespace(), path = res.getPath();
        return "/assets/" + domain + "/" + path;
    }

}
