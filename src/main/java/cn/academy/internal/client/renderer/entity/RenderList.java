package cn.academy.internal.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author WeAthFolD
 *
 */
@SideOnly(Side.CLIENT)
public class RenderList<T extends Entity> extends Render<T> {
    List<Render> renderers = new ArrayList<>();
    
    public RenderList(RenderManager rm, Render ...rs) {
        super(rm);
        renderers.addAll(Arrays.asList(rs));
    }
    
    public RenderList append(Render e) {
        renderers.add(e);
        return this;
    }

    @Override
    public void doRender(T ent, double x,
            double y, double z, float a, float b) {
        for(Render r : renderers)
            r.doRender(ent, x, y, z, a, b);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

}