package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.entity.EntityMdBall;
import cn.lambdalib2.render.legacy.ShaderSimple;
import cn.lambdalib2.template.client.render.RenderIcon;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static cn.academy.internal.entity.EntityMdBall.MAX_TETXURES;

@SideOnly(Side.CLIENT)
public class RenderEntityMdBall extends RenderIcon<EntityMdBall> {
    ResourceLocation[] textures;
    ResourceLocation glowTexture;

    public RenderEntityMdBall(RenderManager manager) {
        super(manager, null);
        textures = Resources.getEffectSeq("mdball", MAX_TETXURES);
        glowTexture = Resources.getTexture("effects/mdball/glow");
        //this.minTolerateAlpha = 0.05f;
        this.shadowOpaque = 0;
    }

    @Override
    public void doRender(EntityMdBall ent, double x, double y, double z, float par8, float par9) {
        if (!ent.updateRenderTick()) return;

        EntityPlayer clientPlayer = Minecraft.getMinecraft().player;

        //HACK: Force set the render pos to prevent glitches
        {
            x = ent.posX - clientPlayer.posX;
            y = ent.posY - clientPlayer.posY + 1.6;
            z = ent.posZ - clientPlayer.posZ;
        }

        GL11.glDepthMask(false);

        GL11.glPushMatrix();
        {
            ShaderSimple.instance().useProgram();
            GL11.glTranslated(ent.offsetX, ent.offsetY, ent.offsetZ);

            float alpha = ent.getAlpha();
            float size = ent.getSize();

            //Glow texture
            this.color.setAlpha(Colors.f2i(alpha * (0.3f + ent.alphaWiggle * 0.7f)));
            this.icon = glowTexture;
            this.setSize(0.7f * size);
            super.doRender(ent, x, y, z, par8, par9);

            //Core
            this.color.setAlpha(Colors.f2i(alpha * (0.8f + 0.2f * ent.alphaWiggle)));
            this.icon = textures[ent.texID];
            this.setSize(0.5f * size);
            super.doRender(ent, x, y, z, par8, par9);
            GL20.glUseProgram(0);
        }
        GL11.glPopMatrix();
    }
}