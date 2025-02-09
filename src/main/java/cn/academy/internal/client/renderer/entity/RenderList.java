package cn.academy.internal.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class RenderList<T extends Entity> extends Render<T> {
    private final List<Render<T>> renderers = new ArrayList<>();

    @SafeVarargs
    public RenderList(RenderManager renderManager, Render<T>... renders) {
        super(renderManager);
        if (renders != null && renders.length > 0) {
            Collections.addAll(renderers, renders);
        }
    }

    public void append(Render<T> renderer) {
        if (renderer != null) {
            renderers.add(renderer);
        }
    }

    @Override
    public void doRender(@Nonnull T entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!renderers.isEmpty()) {
            for (Render<T> renderer : renderers) {
                renderer.doRender(entity, x, y, z, yaw, partialTicks);
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull T entity) {
        return null;
    }
}
