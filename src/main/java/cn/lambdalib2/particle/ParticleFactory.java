package cn.lambdalib2.particle;

import cn.lambdalib2.particle.decorators.ParticleDecorator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Completely redesign the particle system
 * 
 * @author WeAthFolD
 */
public class ParticleFactory extends ParticleFactoryBase {
    public final Particle template;

    private final List<ParticleDecorator> decorators = new ArrayList<>();

    double px, py, pz;
    double vx, vy, vz;

    public ParticleFactory(Particle _template) {
        template = _template;
    }

    public void addDecorator(ParticleDecorator dec) {
        decorators.add(dec);
    }

    public void setPosition(double x, double y, double z) {
        px = x;
        py = y;
        pz = z;
    }

    public void setVelocity(double x, double y, double z) {
        vx = x;
        vy = y;
        vz = z;
    }

    /**
     * Generate a specific particle in the world with the current position and
     * velocity settings.
     */
    @Override
    public Particle next(World world) {
        Particle p = this.queryParticle();
        p.world = world;
        p.fromTemplate(template);

        p.setPosition(px, py, pz);

        p.motionX = vx;
        p.motionY = vy;
        p.motionZ = vz;

        for (ParticleDecorator pd : decorators)
            pd.decorate(p);

        return p;
    }

    /**
     * Generates a particle at the given world position with the given velocity.
     * Note that this will override the current position and velocity settings.
     */
    public Particle next(World world, Vec3d position, Vec3d velocity) {
        px = position.x;
        py = position.y;
        pz = position.z;

        vx = velocity.x;
        vy = velocity.y;
        vz = velocity.z;

        return next(world);
    }
}
