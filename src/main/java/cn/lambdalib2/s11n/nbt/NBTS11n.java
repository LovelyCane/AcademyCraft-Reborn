package cn.lambdalib2.s11n.nbt;

import cn.academy.internal.ability.Controllable;
import cn.academy.internal.datapart.PresetData;
import cn.lambdalib2.s11n.SerializationHelper;
import cn.lambdalib2.s11n.SerializeDynamic;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Handles NBT (Notch Bull**** Tag) serialization.
 *
 * @author WeAthFolD
 */
public class NBTS11n {

    private static final BaseSerializer<NBTTagByte, Enum> enumSer = new BaseSerializer<NBTTagByte, Enum>() {
        @Override
        public NBTTagByte write(Enum value) {
            Preconditions.checkArgument(value.ordinal() < Byte.MAX_VALUE);
            return new NBTTagByte((byte) value.ordinal());
        }

        @Override
        public Enum read(NBTTagByte tag, Class<? extends Enum> type) {
            return type.getEnumConstants()[tag.getByte()];
        }
    };
    private static SerializationHelper helper = new SerializationHelper();
    private static Map<Class, BaseSerializer> baseSerializerMap = new HashMap<>();
    private static Map<Class, CompoundSerializer> compoundSerializerMap = new HashMap<>();
    private static Map<Class, Supplier<?>> supplierMap = new HashMap<>();

    static {
        {
            BaseSerializer<NBTTagByte, Byte> ser = new BaseSerializer<NBTTagByte, Byte>() {
                @Override
                public NBTTagByte write(Byte value) {
                    return new NBTTagByte(value);
                }

                @Override
                public Byte read(NBTTagByte tag, Class<? extends Byte> type) {
                    return tag.getByte();
                }
            };
            addBase(byte.class, ser);
            addBase(Byte.class, ser);
        }
        {
            BaseSerializer<NBTTagShort, Short> ser = new BaseSerializer<NBTTagShort, Short>() {
                @Override
                public NBTTagShort write(Short value) {
                    return new NBTTagShort(value);
                }

                @Override
                public Short read(NBTTagShort tag, Class<? extends Short> type) {
                    return tag.getShort();
                }
            };
            addBase(short.class, ser);
            addBase(Short.class, ser);
        }
        {
            BaseSerializer<NBTTagInt, Integer> ser = new BaseSerializer<NBTTagInt, Integer>() {
                @Override
                public NBTTagInt write(Integer value) {
                    return new NBTTagInt(value);
                }

                @Override
                public Integer read(NBTTagInt tag, Class<? extends Integer> type) {
                    return tag.getInt();
                }
            };
            addBase(int.class, ser);
            addBase(Integer.class, ser);
        }
        {
            BaseSerializer<NBTTagFloat, Float> ser = new BaseSerializer<NBTTagFloat, Float>() {
                @Override
                public NBTTagFloat write(Float value) {
                    return new NBTTagFloat(value);
                }

                @Override
                public Float read(NBTTagFloat tag, Class<? extends Float> type) {
                    return tag.getFloat();
                }
            };
            addBase(float.class, ser);
            addBase(Float.class, ser);
        }
        {
            BaseSerializer<NBTTagDouble, Double> ser = new BaseSerializer<NBTTagDouble, Double>() {
                @Override
                public NBTTagDouble write(Double value) {
                    return new NBTTagDouble(value);
                }

                @Override
                public Double read(NBTTagDouble tag, Class<? extends Double> type) {
                    return tag.getDouble();
                }
            };
            addBase(double.class, ser);
            addBase(Double.class, ser);
        }
        {
            BaseSerializer<NBTTagLong, Long> ser = new BaseSerializer<NBTTagLong, Long>() {
                @Override
                public NBTTagLong write(Long value) {
                    return new NBTTagLong(value);
                }

                @Override
                public Long read(NBTTagLong tag, Class<? extends Long> type) {
                    return tag.getLong();
                }
            };
            addBase(long.class, ser);
            addBase(Long.class, ser);
        }
        {
            BaseSerializer<NBTTagByte, Boolean> ser = new BaseSerializer<NBTTagByte, Boolean>() {
                @Override
                public NBTTagByte write(Boolean value) {
                    return new NBTTagByte((byte) (value ? 1 : 0));
                }

                @Override
                public Boolean read(NBTTagByte tag, Class<? extends Boolean> type) {
                    return tag.getByte() != 0;
                }
            };
            addBase(boolean.class, ser);
            addBase(Boolean.class, ser);
        }
        {
            BaseSerializer<NBTTagByteArray, byte[]> ser = new BaseSerializer<NBTTagByteArray, byte[]>() {
                @Override
                public NBTTagByteArray write(byte[] value) {
                    return new NBTTagByteArray(value);
                }

                @Override
                public byte[] read(NBTTagByteArray tag, Class<? extends byte[]> type) {
                    return tag.getByteArray();
                }
            };
            addBase(byte[].class, ser);
        }
        {
            BaseSerializer<NBTTagIntArray, int[]> ser = new BaseSerializer<NBTTagIntArray, int[]>() {
                @Override
                public NBTTagIntArray write(int[] value) {
                    return new NBTTagIntArray(value);
                }

                @Override
                public int[] read(NBTTagIntArray tag, Class<? extends int[]> type) {
                    return tag.getIntArray();
                }
            };
            addBase(int[].class, ser);
        }
        {
            BaseSerializer<NBTTagString, String> ser = new BaseSerializer<NBTTagString, String>() {
                @Override
                public NBTTagString write(String value) {
                    return new NBTTagString(value);
                }

                @Override
                public String read(NBTTagString tag, Class<? extends String> type) {
                    return tag.getString();
                }
            };
            addBase(String.class, ser);
        }
        {
            CompoundSerializer<NBTTagCompound> ser = new CompoundSerializer<NBTTagCompound>() {
                Field tagMapField;

                {
                    tagMapField = ReflectionUtils.getObfField(NBTTagCompound.class, "tagMap", "field_74784_a");
                    tagMapField.setAccessible(true);
                }

                @Override
                public void write(NBTTagCompound tag, NBTTagCompound value) {
                    move(value, tag);
                }

                @Override
                public void read(NBTTagCompound tag, NBTTagCompound value) {
                    move(tag, value);
                }

                @SuppressWarnings("unchecked")
                private void move(NBTTagCompound from, NBTTagCompound to) {
                    try {
                        Map<String, NBTBase> map = (Map) tagMapField.get(from);
                        for (String id : map.keySet()) {
                            to.setTag(id, from.getTag(id));
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            addCompound(NBTTagCompound.class, ser);
        }
        { // Collection
            addSupplier(Collection.class, ArrayList::new);
            addSupplier(List.class, ArrayList::new);
            addSupplier(Set.class, HashSet::new);
            CompoundSerializer<Collection> ser = new CompoundSerializer<Collection>() {
                @Override
                public void write(NBTTagCompound tag, Collection value) {
                    NBTTagList ret = new NBTTagList();

                    for (Object obj : value) {
                        ret.appendTag(writeDynamic(obj));
                    }

                    tag.setTag("l", ret);
                }

                @Override
                public void read(NBTTagCompound tag_, Collection value) {
                    value.clear();

                    NBTTagList tag = (NBTTagList) tag_.getTag("l");
                    for (int i = 0; i < tag.tagCount(); ++i) {
                        value.add(readDynamic(tag.getCompoundTagAt(i)));
                    }
                }
            };
            addCompound(Collection.class, ser);
        }
        { // Map
            addSupplier(Map.class, HashMap::new);
            CompoundSerializer<Map<?, ?>> ser = new CompoundSerializer<Map<?, ?>>() {
                @Override
                public void write(NBTTagCompound tag, Map<?, ?> value) {
                    NBTTagList ret = new NBTTagList();

                    for (Entry ent : value.entrySet()) {
                        NBTTagCompound kvpair = new NBTTagCompound();
                        kvpair.setTag("k", writeDynamic(ent.getKey()));
                        kvpair.setTag("v", writeDynamic(ent.getValue()));

                        ret.appendTag(kvpair);
                    }

                    tag.setTag("l", ret);
                }

                @Override
                public void read(NBTTagCompound tag_, Map<?, ?> map) {
                    map.clear();

                    Map erasedMap = (Map) map;
                    NBTTagList tag = (NBTTagList) tag_.getTag("l");

                    for (int i = 0; i < tag.tagCount(); ++i) {
                        NBTTagCompound tag2 = tag.getCompoundTagAt(i);

                        Object key = readDynamic(tag2.getCompoundTag("k"));
                        Object value = readDynamic(tag2.getCompoundTag("v"));

                        erasedMap.put(key, value);
                    }
                }
            };
            addCompound(Map.class, (CompoundSerializer) ser);
        }
        {
            addBase(BitSet.class, new BaseSerializer<NBTBase, BitSet>() {
                @Override
                public NBTBase write(BitSet value) {
                    return new NBTTagByteArray(value.toByteArray());
                }

                @Override
                public BitSet read(NBTBase tag, Class<? extends BitSet> type) {
                    return BitSet.valueOf(((NBTTagByteArray) tag).getByteArray());
                }
            });
        }
        NBTS11n.addBase(PresetData.Preset.class, new BaseSerializer<NBTBase, PresetData.Preset>() {
            @Override
            public NBTBase write(PresetData.Preset value) {
                NBTTagCompound tag = new NBTTagCompound();
                IntStream.range(0, PresetData.MAX_KEYS).forEach(idx -> {
                    Controllable ctrl = value.data[idx];
                    if (ctrl != null) {
                        tag.setTag(String.valueOf(idx), NBTS11n.writeBase(ctrl, Controllable.class));
                    }
                });
                return tag;
            }

            @Override
            public PresetData.Preset read(NBTBase tag_, Class<? extends PresetData.Preset> type) {
                NBTTagCompound tag = (NBTTagCompound) tag_;

                Controllable[] data = new Controllable[PresetData.MAX_KEYS];
                IntStream.range(0, PresetData.MAX_KEYS).forEach(idx -> {
                    String tagName = String.valueOf(idx);
                    if (tag.hasKey(tagName)) {
                        data[idx] = NBTS11n.readBase(tag.getTag(tagName), Controllable.class);
                    }
                });

                return new PresetData.Preset(data);
            }
        });
    }

    /**
     * Writes the object to given {@link NBTTagCompound}.
     * If the object is recursive (no custom helper that handles obj has been added via
     * {@link #addCompound(Class, CompoundSerializer)}, fields according to the rules of core API will be written.
     * Otherwise, the custom helper will be used to write the object.
     */
    @SuppressWarnings("unchecked")
    public static void write(NBTTagCompound tag, Object obj) {
        Preconditions.checkNotNull(obj);

        final Class<?> type = obj.getClass();
        CompoundSerializer serializer = _compound(type);
        if (serializer != null) {
            serializer.write(tag, obj);
        } else { // recursive
            List<Field> fields = helper.getExposedFields(type);
            for (Field f : fields) {
                try {
                    String fieldName = f.getName();
                    Object value = f.get(obj);
                    if (value != null) {
                        if (f.isAnnotationPresent(SerializeDynamic.class)) {
                            tag.setTag(fieldName, writeDynamic(value));
                        } else {
                            tag.setTag(fieldName, writeBase(value));
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException ex) {
                    Debug.error("Error writing field " + f + " in object " + obj);
                }
            }
        }
    }

    /**
     * Reads the object from given {@link NBTTagCompound}
     * If the object is recursive (no custom helper that handles obj has been added via
     * {@link #addCompound(Class, CompoundSerializer)}, fields according to the rules of core API will be read.
     * Otherwise, the custom helper will be used to read the object.
     */
    @SuppressWarnings("unchecked")
    public static void read(NBTTagCompound tag, Object obj) {
        Preconditions.checkNotNull(obj);

        final Class<?> type = obj.getClass();
        CompoundSerializer serializer = _compound(type);
        if (serializer != null) {
            serializer.read(tag, obj);
        } else { // recursive
            List<Field> fields = helper.getExposedFields(type);
            for (Field f : fields) {
                try {
                    String fieldName = f.getName();
                    NBTBase base = tag.getTag(fieldName);
                    if (base != null) {
                        if (f.isAnnotationPresent(SerializeDynamic.class)) {
                            f.set(obj, readDynamic((NBTTagCompound) base));
                        } else {
                            f.set(obj, readBase(base, f.getType()));
                        }
                    }
                } catch (IllegalAccessException | RuntimeException ex) {
                    ex.printStackTrace();
                    // LambdaLib.log.error("Error reading field " + f + " in object " + obj, ex);
                }
            }
        }
    }

    public static NBTBase writeBase(Object obj) {
        return writeBase(obj, obj.getClass());
    }

    @SuppressWarnings("unchecked")
    public static NBTBase writeBase(Object obj, Class<?> type) {
        Preconditions.checkNotNull(obj);

        if (type.isEnum()) {
            return enumSer.write((Enum) obj);
        }

        BaseSerializer serializer = _base(type);

        if (serializer != null) {
            return serializer.write(obj);
        } else if (type.isArray()) {
            Class baseType = type.getComponentType();

            NBTTagCompound tag = new NBTTagCompound();

            int length = Array.getLength(obj);
            tag.setInteger("size", length);
            for (int i = 0; i < length; ++i) {
                tag.setTag(String.valueOf(i), writeBase(Array.get(obj, i), baseType));
            }

            return tag;
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            write(tag, obj);
            return tag;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readBase(NBTBase base, Class<T> type) {
        Preconditions.checkNotNull(base);

        if (type.isEnum()) {
            return (T) enumSer.read((NBTTagByte) base, (Class) type);
        }

        BaseSerializer serializer = _base(type);
        if (serializer != null) {
            return (T) serializer.read(base, type);
        } else if (type.isArray()) {
            NBTTagCompound tag = (NBTTagCompound) base;
            Class baseType = type.getComponentType();
            int size = tag.getInteger("size");
            Object array = Array.newInstance(baseType, size);

            for (int i = 0; i < size; ++i) {
                Array.set(array, i, readBase(tag.getTag(String.valueOf(i)), baseType));
            }
            return (T) array;
        } else if (base instanceof NBTTagCompound) {
            NBTTagCompound tag = (NBTTagCompound) base;
            T instance = instantiate(type);

            read(tag, instance);

            return instance;
        } else throw new RuntimeException("Doesn't support tag type " + base);
    }

    public static <T> void addBase(Class<T> type, BaseSerializer<?, T> serializer) {
        baseSerializerMap.put(type, serializer);
        helper.regS11nType(type);
    }

    public static <T> void addCompound(Class<T> type, CompoundSerializer<? super T> serializer) {
        compoundSerializerMap.put(type, serializer);
        helper.regS11nType(type);
    }

    public static <T> void addSupplier(Class<T> type, Supplier<? extends T> supplier) {
        supplierMap.put(type, supplier);
    }

    public static NBTTagCompound writeDynamic(Object obj) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("t", obj.getClass().getCanonicalName());
        tag.setTag("d", writeBase(obj));

        return tag;
    }

    public static Object readDynamic(NBTTagCompound tag) {
        try {
            String klass = tag.getString("t");
            Class type = Class.forName(klass);
            return readBase(tag.getTag("d"), type);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> T instantiate(Class<T> type) {
        if (supplierMap.containsKey(type)) {
            return (T) supplierMap.get(type).get();
        }
        try {
            Constructor<T> ctor = type.getConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Make given type to be exposed in recursive s11n. Subtypes not included.
     */
    public static void regS11nType(Class<?> type) {
        helper.regS11nType(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> BaseSerializer<?, T> _base(Class<T> type) {
        return baseSerializerMap.get(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> CompoundSerializer<? super T> _compound(Class<T> type) {
        Class<? super T> cur = type;
        while (cur != null) {
            if (compoundSerializerMap.containsKey(cur)) {
                return compoundSerializerMap.get(cur);
            }
            cur = cur.getSuperclass();
        }
        // Interfaces
        for (Class c : type.getInterfaces()) {
            CompoundSerializer<? super T> test = _compound(c);
            if (test != null) {
                return test;
            }
        }
        return null;
    }

    public interface BaseSerializer<NBT extends NBTBase, T> {
        NBT write(T value);

        T read(NBT tag, Class<? extends T> type);
    }

    public interface CompoundSerializer<T> {
        void write(NBTTagCompound tag, T value);

        void read(NBTTagCompound tag, T value);
    }

}
