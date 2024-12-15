/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.util;

import cn.lambdalib2.s11n.network.NetworkMessage;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper to help syncing fields within entity, which gets rid of the ANNOYING 
 * registering proccess. Supports all the type that is supported by DataWatcher. <br>
 * You should delegate the init() method within entityInit(), and update() method within onUpdate().
 * <br> The direction is always server -> client.
 * <br> The registered fields should be symmetric in two sides so that we can track the ID correctly.
 *
 * <br> Currently EntitySyncer supports the following types:
 * <code>
 * <br>  * int, Integer
 * <br>  * float, Float
 * <br>  * short, Short
 * <br>  * byte, Byte
 * <br>  * String
 * <br>  * Entity
 * <br>  * ChunkCoordinates
 * <br>  * ItemStack
 * </code>
 * <br> More commonly used types will be added soon.
 * @author WeAthFolD
 */
public class EntitySyncer {

    private static final Map<Class<?>, Type> typeMap = new HashMap<>();
    private static final Map<Class<?>, DataSerializer> serializerMap = new HashMap<>();
    private static final Method mGetWatchedObject = ReflectionUtils.getObfMethod(EntityDataManager.class, "get", "func_187225_a", DataParameter.class);
    private static final Fetcher
            defaultFetcher = (EntitySyncer d, DataParameter<Object> dataParameter) -> (getWatchedObject(d, dataParameter)),
            entityFetcher = (EntitySyncer d, DataParameter<Object> dataParameter) -> {
                Integer i = (Integer) defaultFetcher.supply(d, dataParameter);
                if (i == null) return null;

                return d.entity.world.getEntityByID(i);
            };
    private static final Creator
            byteCreator = (Object b) -> (byte) b,
            shortCreator = (Object b) -> (short) b,
            intCreator = (Object b) -> (int) b,
            floatCreator = (Object b) -> (float) b,
            stringCreator = (Object b) -> b.toString(),
            itemStackCreator = (Object s) -> ((ItemStack) s).copy(),
            booleanCreator = (Object b) -> (boolean) b;
    static {
        put(byteCreator, (byte) 0, Byte.class, byte.class);
        put(shortCreator, (short) 0, Short.class, short.class);
        put(intCreator, 0, Integer.class, int.class);
        put(floatCreator, 0.0f, Float.class, float.class);
        put(stringCreator, (String) null, String.class);
        put(itemStackCreator, (ItemStack) null, ItemStack.class);
        put(booleanCreator, (boolean) false, Boolean.class);
//        put(ccCreator, (ChunkCoordinates)null, ChunkCoordinates.class);

        serializerMap.put(Byte.class, DataSerializers.BYTE);
        serializerMap.put(byte.class, DataSerializers.BYTE);
        serializerMap.put(Short.class, DataSerializers.VARINT);
        serializerMap.put(short.class, DataSerializers.VARINT);
        serializerMap.put(Integer.class, DataSerializers.VARINT);
        serializerMap.put(int.class, DataSerializers.VARINT);
        serializerMap.put(Float.class, DataSerializers.FLOAT);
        serializerMap.put(float.class, DataSerializers.FLOAT);
        serializerMap.put(String.class, DataSerializers.STRING);
//        serializerMap.put(ITextComponent.class, DataSerializers.TEXT_COMPONENT);
        serializerMap.put(ItemStack.class, DataSerializers.ITEM_STACK);
        //todo: Optional<IBlockState>
        serializerMap.put(Boolean.class, DataSerializers.BOOLEAN);
        serializerMap.put(boolean.class, DataSerializers.BOOLEAN);
//        serializerMap.put(Rotations.class, DataSerializers.ROTATIONS);
//        serializerMap.put(BlockPos.class, DataSerializers.BLOCK_POS);
        //todo: Optional<BlockPos>
//        serializerMap.put(EnumFacing.class, DataSerializers.FACING);
        //todo: Optional<UUID>
//        serializerMap.put(NBTTagCompound.class, DataSerializers.COMPOUND_TAG);


//        serializerMap.put(ChunkCoordinates.class, 6);
//
//        serializerMap.put(Entity.class, 2);

    }

    private final Entity entity;
    private final EntityDataManager dataManager;
    private final List<SyncInstance> watched;
    private boolean firstUpdate;

    public EntitySyncer(Entity ent) {
        entity = ent;
        try {
            dataManager = (EntityDataManager) ReflectionUtils.getObfField(Entity.class, "dataManager", "field_70180_af").get(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        watched = new ArrayList<>();
    }

    private static void put(Creator c, Fetcher f, Object d, Class... classes) {
        for (Class cc : classes)
            typeMap.put(cc, new Type(c, f, d));
    }

    private static void put(Creator c, Object d, Class... classes) {
        put(c, defaultFetcher, d, classes);
    }
//        ccCreator = (Object s) -> ((ChunkCoordinates)s),
//        entityCreator = (Object s) -> ((Entity)s).getEntityId();

    private static Object getWatchedObject(EntitySyncer w, DataParameter<Object> dataParameter) {
        try {
            return (Object) mGetWatchedObject.invoke(w.dataManager, dataParameter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delegated when the entity enters entityInit().
     */
    public void init() {
        for (Field f : entity.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Synchronized.class)) {
                Synchronized anno = f.getAnnotation(Synchronized.class);

                f.setAccessible(true);
                watched.add(new SyncInstance(entity.getClass(), f, anno));
            }
        }
    }

    /**
     * Delegated during entity onUpdate() tick.
     */
    public void update() {
        if (!firstUpdate) {
            firstUpdate = true;
            for (SyncInstance si : watched) {
                si.init();
            }
        } else {
            for (SyncInstance si : watched) {
                si.tick();
            }
        }
    }


    public enum SyncType {
        /**
         * This field is only synchronized on startup.
         */
        ONCE,

        /**
         * This field is synchronized every tick when entity is alive.
         */
        RUNTIME
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Synchronized {

        SyncType value() default SyncType.RUNTIME;

        boolean allowNull() default false;

    }

    interface Test {
        void call(Object o);
    }


    private interface Creator<T, U> {
        T supply(U value);
    }

    private interface Fetcher {
        Object supply(EntitySyncer d, DataParameter<Object> dataParameter);
    }

    private static class Type {
        Creator creator;
        Fetcher fetcher;
        Object defaultValue;

        public Type(Creator _c, Fetcher _f, Object _d) {
            creator = _c;
            fetcher = _f;
            defaultValue = _d;
        }
    }

    private class SyncInstance {

        protected final DataParameter<Object> dataParameter;
        protected final Field field;

        protected final Creator c;
        protected final Fetcher f;

        Synchronized anno;

        public SyncInstance(Class<? extends Entity> clazz, Field f, Synchronized _anno) {

            field = f;
            anno = _anno;

            Type t = null;
            Class typeClazz = f.getType();
            while (t == null && typeClazz != null) {
                t = typeMap.get(typeClazz);
                if (t == null)
                    typeClazz = typeClazz.getSuperclass();
            }

            if (t == null)
                throw new UnsupportedOperationException("Unsupported sync type " + f.getType());

            c = t.creator;
            this.f = t.fetcher;

            DataSerializer<Object> serial = serializerMap.get(typeClazz);

            dataParameter = EntityDataManager.createKey(clazz, serial);
            try
            {
                dataManager.get(dataParameter);
            }
            catch(NullPointerException npe)
            {
                dataManager.register(dataParameter, 0);
                Debug.log("Entry is missing, try register it.");
            }

            Object val = convert();
            if (val == null)
                val = t.defaultValue;

            dataManager.set(dataParameter, val);
        }

        void init() {
            updateAll(true);
        }

        protected Object convert() {
            try {
                return c.supply(field.get(entity));
            } catch (Exception e) {
                return null;
            }
        }

        void tick() {
            updateAll(anno.value() == SyncType.RUNTIME);
        }

        private void updateAll(boolean doServer) {
            //System.out.println("Synchronizing " + field.getName());
            try {
                if (entity.world.isRemote) {
                    Object obj = f.supply(EntitySyncer.this, dataParameter);

                    if (obj != null || anno.allowNull()) {
                        field.set(entity, obj);
                    }
                } else {
                    if (doServer) {
                        Object o = convert();
                        if (o != null) {
                            dataManager.set(dataParameter, o);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Sync failed");
            }
        }

    }
}
