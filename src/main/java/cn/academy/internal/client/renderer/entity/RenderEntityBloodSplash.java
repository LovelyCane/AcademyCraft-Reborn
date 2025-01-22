package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.entity.EntityBloodSplash;
import cn.lambdalib2.template.client.render.RenderIcon;
import cn.lambdalib2.util.MathUtils;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.entity.EntityBloodSplash.SPLASH;
import static org.lwjgl.opengl.GL11.glDepthMask;

@SideOnly(Side.CLIENT)
public class RenderEntityBloodSplash extends RenderIcon<EntityBloodSplash> {
    public RenderEntityBloodSplash(RenderManager manager) {
        super(manager, null);
        setSize(1.0f);
        this.color.set(213, 29, 29, 200);
    }

    @Override
    public void doRender(EntityBloodSplash entity, double x, double y, double z, float a, float b) {
        icon = (SPLASH[MathUtils.clampi(0, SPLASH.length - 1, entity.frame)]);
        this.size = entity.getSize();

        glDepthMask(false);
        super.doRender(entity, x, y, z, a, b);
        glDepthMask(true);
    }
}
