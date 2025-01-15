package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.lambdalib2.util.GameTimer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class TornadoEffect {

    public static final int DIVIDE = 40;

    public static class Ring {
        public final double y;
        public final double width;
        public final double phase;
        public final double sizeScale;

        public Ring(double y, double width, double phase, double sizeScale) {
            this.y = y;
            this.width = width;
            this.phase = phase;
            this.sizeScale = sizeScale;
        }
    }

    private final List<Ring> rings = new ArrayList<>();
    public final double ht;
    public final double sz;
    public final double dscale;
    private final double timeOffset;

    public double alpha = 1.0;

    public TornadoEffect(double ht, double sz, double density, double dscale) {
        this.ht = ht;
        this.sz = sz;
        this.dscale = dscale;
        Random rng = new Random();
        this.timeOffset = rng.nextDouble() * 20;

        double accum = 0.0;
        double stdstep = ht / DIVIDE;

        while (accum < ht) {
            accum += stdstep * (1.0 + rng.nextGaussian() * 0.2);

            if (rng.nextDouble() < density) {
                rings.add(new Ring(accum, stdstep * ranged(1.8, 2.2, rng), rng.nextDouble() * 360, ranged(0.9, 1.2, rng)));

                if (rng.nextDouble() < 0.35) {
                    rings.add(new Ring(accum, stdstep * ranged(1.8, 2.2, rng), rng.nextDouble() * 360, ranged(1.2, 1.7, rng)));
                }
            }
        }
    }

    public double time() {
        return GameTimer.getTime() * 4.0 - timeOffset;
    }

    public List<Ring> getRings() {
        return rings;
    }

    private static double ranged(double min, double max, Random rng) {
        return min + (max - min) * rng.nextDouble();
    }
}