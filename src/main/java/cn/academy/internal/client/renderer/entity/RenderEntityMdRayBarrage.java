package cn.academy.internal.client.renderer.entity;

import cn.academy.internal.entity.EntityMdRayBarrage;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityMdRayBarrage extends RenderEntityMdRaySmall {
    public RenderEntityMdRayBarrage(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(Entity ent, double x, double y, double z, float a, float b) {
        EntityMdRayBarrage ray = (EntityMdRayBarrage) ent;
        ray.onRenderTick();

        float rYaw = ent.rotationYaw, rPitch = ent.rotationPitch;

        for (EntityMdRayBarrage.SubRay sr : ray.subrays) {
            ent.rotationYaw = rYaw + sr.yawOffset;
            ent.rotationPitch = rPitch + sr.pitchOffset;
            this.plainDoRender(ent, x, y, z, a, b);
        }

        ent.rotationYaw = rYaw;
        ent.rotationPitch = rPitch;
    }
}