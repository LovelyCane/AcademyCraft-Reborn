package cn.lambdalib2.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ResourceUtils {
    public static InputStream getResourceStream(final ResourceLocation res) {
        String path = "/assets/" + res.getNamespace() + "/" + res.getPath();
        InputStream stream = ResourceUtils.class.getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("Can't find resource " + res);
        }
        return stream;
    }

    public static URL getURLForResource(final ResourceLocation resourceLocation) {
        URLStreamHandler urlStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
                    }
                };
            }
        };

        try {
            return new URL(null, resourceLocation.toString(), urlStreamHandler);
        } catch (MalformedURLException e) {
            throw new Error("Error creating URL for resource: " + resourceLocation, e);
        }
    }
}