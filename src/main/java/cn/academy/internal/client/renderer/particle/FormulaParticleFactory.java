package cn.academy.internal.client.renderer.particle;

import cn.academy.Resources;
import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.particle.ParticleFactory;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

@SideOnly(Side.CLIENT)
public class FormulaParticleFactory extends ParticleFactory {
    public static final FormulaParticleFactory instance = new FormulaParticleFactory();

    static ResourceLocation[] textures = Resources.getEffectSeq("formula", 10);

    private FormulaParticleFactory() {
        super(new Particle());
        this.template.color.set(220, 220, 220, 255);
        this.template.hasLight = false;

        this.addDecorator(particle -> {
            particle.size = RandUtils.rangef(1, 1.7f);
            particle.color.setAlpha(RandUtils.rangei(152, 384));
            particle.texture = textures[RandUtils.nextInt(textures.length)];
            particle.fadeInTime = 2;
            particle.fadeAfter(RandUtils.rangei(10, 15), 20);
        });
    }
}