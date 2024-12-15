/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.vis;

import cn.lambdalib2.s11n.SerializeType;
import cn.lambdalib2.util.RenderUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@SerializeType
public class CompTransform {

    public static final CompTransform identity = new CompTransform();

    public Vec3d transform = Vec3d.ZERO;
    
    public Vec3d pivotPt = Vec3d.ZERO;
    
    public Vec3d rotation = Vec3d.ZERO;
    
    public double scale = 1.0;
    
    public CompTransform setPivot(double x, double y, double z) {
        pivotPt = new Vec3d(x, y, z);
        return this;
    }
    
    public CompTransform setTransform(double x, double y, double z) {
        transform = new Vec3d(x, y, z);
        return this;
    }
    
    public CompTransform setRotation(double x, double y, double z) {
        rotation = new Vec3d(x, y, z);
        return this;
    }
    
    public CompTransform setScale(double val) {
        scale = val;
        return this;
    }
    
    public void doTransform() {
        RenderUtils.glTranslate(VecUtils.add(transform, pivotPt));

        GL11.glRotated(rotation.x, 1, 0, 0);
        GL11.glRotated(rotation.y, 0, 1, 0);
        GL11.glRotated(rotation.z, 0, 0, 1);
        
        GL11.glScaled(scale, scale, scale);
        
        RenderUtils.glTranslate(VecUtils.neg(pivotPt));
    }
    
}
