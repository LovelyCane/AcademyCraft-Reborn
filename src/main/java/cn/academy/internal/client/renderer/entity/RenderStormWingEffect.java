package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.ability.vanilla.vecmanip.client.effect.StormWingEffect;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.StormWingEffect.SubEffect;
import cn.academy.internal.ability.vanilla.vecmanip.client.effect.TornadoRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderStormWingEffect extends Render<StormWingEffect> {
    public RenderStormWingEffect(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(StormWingEffect entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotated(-entity.rotationYaw, 0, 1, 0);
        GL11.glRotated(entity.rotationPitch * 0.2, 1, 0, 0);
        GL11.glRotated(-70, 1, 0, 0);
        GL11.glTranslated(0, 0.2, -0.5);

        for (SubEffect subEffect : entity.tornadoList) {
            GL11.glPushMatrix();
            subEffect.trans.doTransform();
            TornadoRenderer.doRender(subEffect.eff);
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    @Override
    protected ResourceLocation getEntityTexture(StormWingEffect entity) {
        return null;
    }
}