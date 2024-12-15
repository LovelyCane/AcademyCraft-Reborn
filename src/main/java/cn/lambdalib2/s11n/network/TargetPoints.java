/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.s11n.network;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to convert objects into {@link TargetPoint}.
 *
 * @author WeAthFolD
 */
public class TargetPoints {

    public static final double DEFAULT_RANGE = 16;
    private static Map<Class<?>, TargetPointConverter> converters = new HashMap();

    static {
        addConverter(TargetPoint.class, new TargetPointConverter<TargetPoint>() {

            @Override
            public TargetPoint convert(TargetPoint object, double range) {
                return object;
            }

        });

        addConverter(Entity.class, new TargetPointConverter<Entity>() {

            @Override
            public TargetPoint convert(Entity object, double range) {
                if (range == -1)
                    range = DEFAULT_RANGE;
                return new TargetPoint(object.dimension, object.posX, object.posY, object.posZ, range);
            }

        });

        addConverter(TileEntity.class, new TargetPointConverter<TileEntity>() {

            @Override
            public TargetPoint convert(TileEntity object, double range) {
                if (range == -1)
                    range = DEFAULT_RANGE;
                return new TargetPoint(object.getWorld().provider.getDimension(), object.getPos().getX() + 0.5,
                        object.getPos().getY() + 0.5, object.getPos().getZ() + 0.5, range);
            }

        });
    }

    public static void addConverter(Class<?> klass, TargetPointConverter conv) {
        if (converters.containsKey(klass)) {
            throw new RuntimeException("Cannot add multiple TargetPointConverter for class " + klass);
        }
        converters.put(klass, conv);
    }

    public static TargetPointConverter findConverter(Object obj) {
        Class klass = obj.getClass();
        TargetPointConverter conv;
        while (klass != null) {
            if ((conv = converters.get(klass)) != null)
                return conv;
            klass = klass.getSuperclass();
        }
        return null;
    }

    public static TargetPoint convert(Object obj, double range) {
        TargetPointConverter conv = findConverter(obj);
        if (conv == null) {
            throw new UnsupportedOperationException("Didn't find TargetPoint converter for " + obj);
        }
        return conv.convert(obj, range);
    }

    public interface TargetPointConverter<T> {
        TargetPoint convert(T object, double range);
    }

}
