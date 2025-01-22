package cn.academy.internal.client.renderer.util;


import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeathFolD
 */
@SideOnly(Side.CLIENT)
public interface IPointFactory {
    public Vec3d next();
}