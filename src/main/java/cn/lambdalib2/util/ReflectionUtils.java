package cn.lambdalib2.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation.EnumHolder;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {
    private static final Set<String> removedClasses = new HashSet<>();
    private static final Set<ASMData> removedMethods = new HashSet<>();
    private static ASMDataTable table;

    public static void _init(ASMDataTable _table) {
        table = _table;

        String startSide = FMLCommonHandler.instance().getSide().toString();
        Set<ASMData> sideData = table.getAll("net.minecraftforge.fml.relauncher.SideOnly");
        Set<ASMData> optionalMethods = table.getAll("net.minecraftforge.fml.common.Optional$Method");
        Set<ASMData> optionalClasses = table.getAll("net.minecraftforge.fml.common.Optional$Interface");

        // Process SideOnly annotations
        for (ASMData asmData : sideData) {
            if (Objects.equals(asmData.getClassName(), asmData.getObjectName())) { // Is a class
                EnumHolder enumHolder = (EnumHolder) asmData.getAnnotationInfo().get("value");
                if (!Objects.equals(enumHolder.getValue(), startSide)) {
                    removedClasses.add(asmData.getClassName());
                }
            } else if (asmData.getObjectName().contains("(")) { // Is a method
                String assumedSide = ((EnumHolder) asmData.getAnnotationInfo().get("value")).getValue();
                if (!assumedSide.equals(startSide))
                    removedMethods.add(asmData);
            }
        }


        // Process Optional annotations
        for (ASMData optional : optionalClasses) {
            String modid = (String) optional.getAnnotationInfo().get("modid");
            if (!Loader.isModLoaded(modid) && !ModAPIManager.INSTANCE.hasAPI(modid)) {
                removedClasses.add(optional.getClassName());
            }
        }
        for (ASMData optional : optionalMethods) {
            String modid = (String) optional.getAnnotationInfo().get("modid");
            if (!Loader.isModLoaded(modid) && !ModAPIManager.INSTANCE.hasAPI(modid)) {
                removedMethods.add(optional);
            }
        }
    }


    /**
     * Get all the methods for a class, including those that are private or protected in parent class.
     * All the methods are made accessible.
     */
    public static List<Method> getAccessibleMethods(Class<?> cls) {
        List<Method> ret = new ArrayList<>();
        Set<String> seenMethods = new HashSet<>();

        while (cls != null) {
            for (Method m : cls.getDeclaredMethods()) {
                String methodSignature = m.toString();
                if (seenMethods.add(methodSignature)) { // Avoid duplicate method from parent and child
                    m.setAccessible(true);
                    ret.add(m);
                }
            }
            cls = cls.getSuperclass();
        }

        return ret;
    }

    public static Method getObfMethod(Class<?> cl, String methodName, String obfName, Class<?>... parameterTypes) {
        try {
            Method m;
            try {
                m = cl.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
                m = cl.getDeclaredMethod(obfName, parameterTypes);
            }
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a class field (both in workspace and in builds) by its deobf name and obf name.
     */
    public static Field getObfField(Class<?> cl, String normName, String obfName) {
        try {
            Field f;
            try {
                f = cl.getDeclaredField(normName);
            } catch (NoSuchFieldException ignored) {
                f = cl.getDeclaredField(obfName);
            }
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all methods in all classes with given annotation.
     */
    public static List<Method> getMethods(Class<? extends Annotation> annoClass) {
        List<ASMData> objects = getRawObjects(annoClass.getCanonicalName());

        return objects.stream()
                .map(data -> {
                    try {
                        Class<?> type = Class.forName(data.getClassName());

                        String fullDesc = data.getObjectName();
                        int idx = fullDesc.indexOf('(');
                        String methodName = fullDesc.substring(0, idx);
                        String desc = fullDesc.substring(idx);

                        Type[] rawArgs = Type.getArgumentTypes(desc);
                        Class<?>[] args = new Class[rawArgs.length];
                        for (int i = 0; i < rawArgs.length; ++i) {
                            args[i] = Class.forName(rawArgs[i].getClassName());
                        }

                        return type.getDeclaredMethod(methodName, args);
                    }
                    catch (ClassNotFoundException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }

                }).collect(Collectors.toList());
    }

    /**
     * Get all classes with given annotation.
     */
    public static List<Class<?>> getClasses(Class<? extends Annotation> annoClass) {
        return getRawObjects(annoClass.getCanonicalName()).stream()
                .map(ASMData::getClassName)
                .distinct()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all fields with given annotation.
     */
    public static List<Field> getFields(Class<? extends Annotation> annoClass) {
        List<ASMData> objects = getRawObjects(annoClass.getCanonicalName());
        return objects.stream()
                .filter(obj -> !Objects.equals(obj.getObjectName(), obj.getClassName()))
                .map(it -> {
                    try {
                        Field field =  Class.forName(it.getClassName()).getDeclaredField(it.getObjectName());
                        field.setAccessible(true);
                        return field;

                    } catch (ClassNotFoundException | NoSuchFieldException ex) {
                        throw new RuntimeException(ex);
                    } catch (NoClassDefFoundError ex) {
                        Debug.log(String.format("Error when get field %s.%s ", it.getClassName(), it.getObjectName()));
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public static List<ASMData> getRawObjects(String annoName) {
        return getRawObjects(annoName, true);
    }

    public static List<ASMData> getRawObjects(String annoName, boolean removeSideOnly) {
        Stream<ASMData> stream = table.getAll(annoName).stream();
        if (removeSideOnly) {
            stream = stream.filter(it -> !removedClasses.contains(it.getClassName()))
                    .filter(it -> removedMethods.stream().noneMatch(m -> isClassObjectEqual(it, m)));
        }
        return stream.collect(Collectors.toList());
    }

    private static boolean isClassObjectEqual(ASMData lhs, ASMData rhs) {
        return (Objects.equals(lhs.getObjectName(), rhs.getObjectName())) &&
                (Objects.equals(lhs.getClassName(), rhs.getClassName()));
    }
}