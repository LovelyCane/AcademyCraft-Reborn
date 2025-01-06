package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.vanilla.vecmanip.client.effect.TornadoRenderer;
import cn.lambdalib2.registry.mc.RegEntityRender;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@RegEntityRender(Tornado.class)
@SuppressWarnings("unused")
public class TornadoEntityRenderer extends Render<Tornado> {
    public TornadoEntityRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Tornado entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        TornadoRenderer.doRender(entity.theTornado);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Tornado entity) {
        return null;
    }
}
