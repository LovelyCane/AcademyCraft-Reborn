package cn.academy.internal.client.renderer.entity;

import cn.academy.Resources;
import cn.academy.internal.entity.EntityDiamondShield;
import cn.lambdalib2.render.legacy.LegacyMesh;
import cn.lambdalib2.render.legacy.SimpleMaterial;
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
public class RenderEntityDiamondShield extends Render<EntityDiamondShield> {
    LegacyMesh mesh;
    SimpleMaterial material;

    public RenderEntityDiamondShield(RenderManager manager) {
        super(manager);
        mesh = new LegacyMesh();
        mesh.setVertices(new double[][]{{-1, 0, 0}, {0, -1, 0}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
        mesh.setUVs(new double[][]{{0, 0}, {1, 1}, {0, 0}, {1, 1}, {0, 1}});
        mesh.setTriangles(new int[]{0, 1, 4, 1, 2, 4, 2, 3, 4, 3, 0, 4});

        material = new SimpleMaterial(Resources.getTexture("effects/diamond_shield"));
        material.ignoreLight = true;
    }

    @Override
    public void doRender(EntityDiamondShield entity, double x, double y, double z, float a, float b) {
        if (!entity.firstUpdated())
            return;

        glDisable(GL_CULL_FACE);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_DEPTH_TEST);
        glPushMatrix();

        glTranslated(x, y, z);

        glRotatef(-entity.rotationYaw, 0, 1, 0);
        glRotatef(entity.rotationPitch, 1, 0, 0);
        float s = 1.5f;
        glScalef(s, s, s);

        mesh.draw(material);

        glPopMatrix();
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDiamondShield entity) {
        return null;
    }

}