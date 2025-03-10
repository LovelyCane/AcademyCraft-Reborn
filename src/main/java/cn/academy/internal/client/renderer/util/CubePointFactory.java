package cn.academy.internal.client.renderer.util;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Randomly gens a point on face of a cube with size(w, h, l). (placed at 0, 0, 0)
 * It is guaranteed that every point has the same appearing probablity.
 * @author WeathFolD
 */
@SideOnly(Side.CLIENT)
public class CubePointFactory implements IPointFactory {
    private double w, h, l;
    private static final Random RNG = new Random();
    boolean centered;

    public CubePointFactory(double _w, double _h, double _l) {
        setSize(_w, _h, _l);
    }
    
    public CubePointFactory setCentered(boolean b) {
        centered = b;
        return this;
    }
    
    public void setSize(double _w, double _h, double _l) {
        w = _w;
        h = _h;
        l = _l;
    }

    private int randFace() {
        return RNG.nextInt(6);
    }

    @Override
    public Vec3d next() {
        int face = randFace();
        double a, b;
        double xOffset = 0, zOffset = 0;
        if(centered) {
            xOffset = -w * 0.5;
            zOffset = -l * 0.5;
        }
        switch(face) {
        case 0:
        case 1:
            a = RNG.nextDouble() * w;
            b = RNG.nextDouble() * l;
            return new Vec3d(a + xOffset, face == 0 ? 0 : h, b + zOffset);
        case 2:
        case 3:
            a = RNG.nextDouble() * h;
            b = RNG.nextDouble() * w;
            return new Vec3d(b + xOffset, a, (face == 2 ? 0 : l) + zOffset);
        case 4:
        case 5:
            a = RNG.nextDouble() * h;
            b = RNG.nextDouble() * l;
            return new Vec3d((face == 4 ? 0 : w) + xOffset, a, b + zOffset);
        }
        return null; //Not supposed to happen
    }
}