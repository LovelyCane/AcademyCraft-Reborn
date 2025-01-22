package cn.academy.internal.client.renderer.entity;

import cn.lambdalib2.renderhook.EntityDummy;
import cn.lambdalib2.renderhook.PlayerRenderHook;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class RenderEntityDummy extends Render<EntityDummy> {
    public RenderEntityDummy(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityDummy _entity, double x, double y, double z, float a, float b) {
        glPushMatrix();
        glTranslated(x, y, z);
        
        boolean fp = ViewOptimize.isFirstPerson(_entity);
        
        float yy, ly;
        if(fp) {
            yy = _entity.rotationYawHead;
            ly = _entity.lastRotationYawHead;
        } else {
            yy = _entity.rotationYaw;
            ly = _entity.lastRotationYaw;
        }
        
        float yaw = MathUtils.lerpf(ly, yy, b);
        glRotated(180 - yaw, 0, 1, 0);
        
        // Render hand
        
        if(fp) {
            glRotated(-_entity.rotationPitch, 1, 0, 0);
        } else {
            ViewOptimize.fixThirdPerson();
        }
        
        for(PlayerRenderHook hook : _entity.data.renderers) {
            hook.renderHand(fp);
        }
        glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDummy e) {
        return null;
    }
}
