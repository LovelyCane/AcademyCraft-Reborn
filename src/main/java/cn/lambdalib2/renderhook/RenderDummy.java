package cn.lambdalib2.renderhook;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslated;

import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.ViewOptimize;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * @author WeAthFolD
 */
@RegEntityRender(EntityDummy.class)
public class RenderDummy extends Render {

    public RenderDummy(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity _entity, double x, double y, double z, float a, float b) {
        EntityDummy entity = (EntityDummy) _entity;
        glPushMatrix();
        glTranslated(x, y, z);
        
        boolean fp = ViewOptimize.isFirstPerson(entity);
        
        float yy, ly;
        if(fp) {
            yy = entity.rotationYawHead;
            ly = entity.lastRotationYawHead;
        } else {
            yy = entity.rotationYaw;
            ly = entity.lastRotationYaw;
        }
        
        float yaw = MathUtils.lerpf(ly, yy, b);
        glRotated(180 - yaw, 0, 1, 0);
        
        // Render hand
        
        if(fp) {
            glRotated(-entity.rotationPitch, 1, 0, 0);
        } else {
            ViewOptimize.fixThirdPerson();
        }
        
        for(PlayerRenderHook hook : entity.data.renderers) {
            hook.renderHand(fp);
        }
        glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        return null;
    }

}
