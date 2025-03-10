package cn.academy.internal.client.renderer.particle;

import cn.academy.Resources;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.particle.ParticleFactory;
import cn.lambdalib2.util.RandUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class MdParticleFactory extends ParticleFactory {
    static Particle template = new Particle();

    static {
        template.texture = Resources.getTexture("effects/md_particle");
    }

    public static MdParticleFactory INSTANCE = new MdParticleFactory(template);

    private MdParticleFactory(Particle _template) {
        super(_template);

        this.addDecorator(particle -> {
            int life = RandUtils.rangei(25, 55);
            particle.fadeAfter(life, 20);
            particle.color.setAlpha(RandUtils.rangei(76, 152));
            particle.size = RandUtils.rangef(0.05f, 0.07f);
        });
    }
}