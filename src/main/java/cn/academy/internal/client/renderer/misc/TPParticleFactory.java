package cn.academy.internal.client.renderer.misc;

import cn.academy.Resources;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.particle.ParticleFactory;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class TPParticleFactory extends ParticleFactory {
    public static TPParticleFactory instance = new TPParticleFactory();

    static Particle template;

    static {
        template = new Particle();
        template.texture = Resources.getTexture("effects/tp_particle");
        template.size = 0.1f;
        template.hasLight = false;
        template.color.set(255, 255, 255, 255);
    }

    public TPParticleFactory() {
        super(template);
    }

    @Override
    public Particle next(World world) {
        Particle ret = super.next(world);
        ret.size = RandUtils.rangef(0.1f, 0.2f);
        ret.color.setAlpha(RandUtils.rangei(153, 204));
        ret.fadeAfter(20, 20);
        return ret;
    }
}