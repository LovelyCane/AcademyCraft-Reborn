package cn.academy;

import cn.academy.internal.client.render.entity.RendererMagHook;
import cn.academy.internal.entity.EntityMagHook;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class AcademyCraftEntityRendererList {
    public static final Map<Class<? extends Render<? extends Entity>>, Class<? extends Entity>> ENTITY_RENDER_MAP = new HashMap<>();

    static {
        ENTITY_RENDER_MAP.put(RendererMagHook.class, EntityMagHook.class);
    }

    private AcademyCraftEntityRendererList() {
    }
}
