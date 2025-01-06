package cn.academy.ability.vanilla.generic.client.effect;

import cn.academy.Resources;
import cn.lambdalib2.registry.mc.RegEntityRender;
import cn.lambdalib2.render.legacy.LegacyMesh;
import cn.lambdalib2.render.legacy.LegacyMeshUtils;
import cn.lambdalib2.render.legacy.SimpleMaterial;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.stream.IntStream;
import java.util.List;
import java.util.stream.Collectors;

@RegEntityRender(BloodSprayEffect.class)
@SuppressWarnings("unused")
public class BloodSprayRenderer extends Render<BloodSprayEffect> {
    private final List<ResourceLocation> texGrnd;
    private final List<ResourceLocation> texWall;
    private final LegacyMesh mesh;
    private final SimpleMaterial material;

    public BloodSprayRenderer(RenderManager manager) {
        super(manager);
        this.texGrnd = seq("grnd", 3);
        this.texWall = seq("wall", 3);
        this.mesh = new LegacyMesh();
        LegacyMeshUtils.createBillboard(mesh, -0.5, -0.5, 0.5, 0.5);
        this.material = new SimpleMaterial(null);
    }

    private List<ResourceLocation> seq(String name, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> Resources.getTexture("effects/blood_spray/" + name + "/" + i))
                .collect(Collectors.toList());
    }

    @Override
    public void doRender(BloodSprayEffect entity, double x, double y, double z, float entityYaw, float partialTicks) {
        List<ResourceLocation> textures = entity.isWall() ? texWall : texGrnd;
        ResourceLocation texture = textures.get(entity.getTextureID() % textures.size());

        material.setTexture(texture);
        RenderUtils.loadTexture(texture);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-entity.rotationYaw, 0, 1, 0);
        GL11.glRotatef(-entity.rotationPitch, 1, 0, 0);
        GL11.glTranslated(entity.getPlaneOffset()[0], entity.getPlaneOffset()[1], 0);
        GL11.glScaled(entity.getSize(), entity.getSize(), entity.getSize());
        GL11.glRotated(entity.getRotation(), 0, 0, 1);

        mesh.draw(material);

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    protected ResourceLocation getEntityTexture(BloodSprayEffect entity) {
        return null;
    }
}