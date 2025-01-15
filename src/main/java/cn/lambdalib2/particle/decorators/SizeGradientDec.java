package cn.lambdalib2.particle.decorators;

import cn.lambdalib2.particle.Particle;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.entityx.MotionHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class SizeGradientDec implements ParticleDecorator {
    public float endScale = 0.7f;

    public SizeGradientDec(float es) {
        endScale = es;
    }

    @Override
    public void decorate(Particle particle) {
        particle.addMotionHandler(new MotionHandler<Particle>() {
            float startSize;

            @Override
            public String getID() {
                return "SizeGradientDecorator";
            }

            @Override
            public void onStart() {
                startSize = this.getTarget().size;
            }

            @Override
            public void onUpdate() {
                getTarget().size = startSize * MathUtils.lerpf(1, endScale, ((float) getTarget().getParticleLife() / (float) getTarget().getMaxLife()));
            }
        });
    }
}
