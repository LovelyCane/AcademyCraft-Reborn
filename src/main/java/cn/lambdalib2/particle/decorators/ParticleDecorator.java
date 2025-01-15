package cn.lambdalib2.particle.decorators;

import cn.lambdalib2.particle.Particle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public interface ParticleDecorator {
    void decorate(Particle particle);
}
