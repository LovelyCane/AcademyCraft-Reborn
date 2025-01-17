package cn.academy.internal.client.renderer.tileentity;

import cn.academy.Resources;
import cn.academy.internal.tileentity.TilePhaseGen;
import cn.lambdalib2.render.obj.ObjLegacyRender;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author WeAthFolD
 */
public class TilePhaseGenRenderer extends TileEntitySpecialRenderer<TilePhaseGen> {
    ObjLegacyRender model;
    ResourceLocation[] textures;
    
    public TilePhaseGenRenderer() {
        model = Resources.getModel("ip_gen");
        textures = Resources.getTextureSeq("models/ip_gen", 5);
    }

    @Override
    public void render(TilePhaseGen gen,
        double x, double y, double z, float partialTicks, int destroyStage, float wtf) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        int tid = MathUtils.clampi(0, 4, 
            (int) Math.round(4.0 * gen.getLiquidAmount() / gen.getTankSize()));
        RenderUtils.loadTexture(textures[tid]);
        model.renderAll();
        GL11.glPopMatrix();
    }

}