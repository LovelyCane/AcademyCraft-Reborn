/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.util;

import cn.lambdalib2.LambdaLib2;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.registry.mc.RegEventHandler;
import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Overrides (disables) key in vanilla minecraft.
 * Use {@link #override(String, int...)} to add an override group, and use
 * {@link #endOverride(String)} to end one.
 * Complete override (all keys) is also supported.
 *
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class ControlOverrider {

    private static IntHashMap<Collection<KeyBinding>> kbMap;
    private static KeyModifier activeModifier;
    private static Field pressedField;
    private static Field kbMapField;

    private static Map<Integer, Override> activeOverrides = new HashMap<>();
    private static Map<String, OverrideGroup> overrideGroups = new HashMap<>();

    private static boolean completeOverriding;
    private static GuiScreen lastTickGui;

    @StateEventCallback
    private static void init(FMLInitializationEvent ev) {
        try {
//            kbMapField = ReflectionUtils.getObfField(KeyBinding.class, "HASH", "field_74514_b");
            kbMapField = ReflectionUtils.getObfField(KeyBindingMap.class, "map", "field_180218_a");

            Method getActiveModifier = ReflectionUtils.getObfMethod(KeyModifier.class, "getActiveModifier", "");
            //fixme: maybe wrong here
            activeModifier = (KeyModifier) getActiveModifier.invoke(null);
            //Init in this stage will cause KeyModifier.None, which means we can only support simple key at one time, maybe.

            kbMap = getOriginalKbMap();

            pressedField = ReflectionUtils.getObfField(KeyBinding.class, "pressed", "field_74513_e");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(kbMapField, kbMapField.getModifiers() & (~Modifier.FINAL));
        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }

    private static IntHashMap createCopy(IntHashMap from, IntHashMap to) {
        if (to == null)
            to = new IntHashMap();
        // Awkward, but who knows if this is faster than reflection?
        for (int i = -100; i <= 250; ++i) {
            if (from.containsItem(i))
                to.addKey(i, from.lookup(i));
        }
        return to;
    }

    // SUPERHACKTECH Starts

    private static IntHashMap getOriginalKbMap() {
        try {
            EnumMap map = (EnumMap) kbMapField.get(null);
            return (IntHashMap) map.get(activeModifier);
//            return (IntHashMap) kbMapField.get(null);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * A complete override stops ALL minecraft keys to function. Currently you can open up complete override globally only once.
     */
    public static void startCompleteOverride() {
        if (!completeOverriding) {
            completeOverriding = true;
            kbMap = createCopy(kbMap, null);
            getOriginalKbMap().clearMap();
        }
    }
    // SUPERHACKTECH Ends

    public static void endCompleteOverride() {
        if (completeOverriding) {
            completeOverriding = false;
            createCopy(kbMap, getOriginalKbMap());
            kbMap = getOriginalKbMap();
        } else {
            throw error("Try to stop complete override while not overriding at all");
        }
    }

    /**
     * Activates an override group. The previous group with given name is ended.
     */
    public static void override(String name, int... keys) {
        overrideGroups.put(name, new OverrideGroup(keys));
        rebuild();
    }

    /**
     * Ends an override group.
     */
    public static void endOverride(String name) {
        Optional.of(overrideGroups.get(name)).ifPresent(OverrideGroup::end);
    }

    private static void rebuild() {
        clearInternal();

        Set<Integer> keys = new HashSet<>();

        Collection<OverrideGroup> groups = overrideGroups.values();
        Iterator<OverrideGroup> iter = groups.iterator();
        while (iter.hasNext()) {
            OverrideGroup group = iter.next();
            if (group.ended) {
                iter.remove();
            } else {
                for (int i : group.keys) {
                    keys.add(i);
                }
            }
        }

        for (int keyid : keys) {
            Collection<KeyBinding> list = kbMap.removeObject(keyid);
            if(list==null)
                continue;
            for(KeyBinding kb : list)//object KeyBinding
            {
                if (kb != null) {
                    try {
                        pressedField.set(kb, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //kb.setKeyCode(-1);
                    activeOverrides.put(keyid, new Override(kb));
                    log("Override new [" + keyid + "]");
                } else {
                    log("Override ignored [" + keyid + "]");
                }
            }
        }
    }

    private static void clearInternal() {
        activeOverrides.entrySet().forEach(entry -> {
            Override ovr = entry.getValue();
            int keyid = entry.getKey();

            ovr.kb.setKeyCode(keyid);
            if(!kbMap.containsItem(keyid))
                kbMap.addKey(keyid, new ArrayList<>() );
            Collection<KeyBinding> collection = kbMap.lookup(keyid);
            if(collection!=null) {
                collection.add(ovr.kb);
                log("Override remove [" + keyid + "]");
            }
            else{
                log("Clear ignore ["+ keyid + "]");
            }
        });

        activeOverrides.clear();
    }

    private static void releaseLocks() {
        for (Map.Entry<Integer, Override> ao : activeOverrides.entrySet()) {
            Collection<KeyBinding> collection = kbMap.lookup(ao.getKey());
            if(collection!=null) {
                collection.add(ao.getValue().kb);
            }
        }
    }

    private static void restoreLocks() {
        for (Map.Entry<Integer, Override> ao : activeOverrides.entrySet()) {
            try {
                pressedField.set(ao.getValue().kb, false);
            } catch (Exception e) {
                Throwables.propagate(e);
            }
            Collection<KeyBinding> collection = kbMap.lookup(ao.getKey());
            if(collection!=null) {
                collection.remove(ao.getValue().kb);
            }
        }
    }

    private static void log(String s) {
        if (LambdaLib2.DEBUG)
            Debug.log(s);
    }

    private static RuntimeException error(String s) {
        return new RuntimeException("ControlOverrider error: " + s);
    }

    @SideOnly(Side.CLIENT)
    public enum Events {
        @RegEventHandler
        instance_;

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent cte) {
            GuiScreen cgs = Minecraft.getMinecraft().currentScreen;
            if (lastTickGui == null && cgs != null) {
                releaseLocks();
            }
            if (lastTickGui != null && cgs == null) {
                restoreLocks();
            }
            lastTickGui = cgs;
        }

        @SubscribeEvent
        public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
            if (SideUtils.isClient()) {
                clearInternal();
                endCompleteOverride();
                overrideGroups.clear();
            }
        }
    }

    private static class Override {
        final KeyBinding kb;

        public Override(KeyBinding _kb) {
            kb = _kb;
        }
    }

    /**
     * A group of key overrides with lifetime.
     */
    @SideOnly(Side.CLIENT)
    private static final class OverrideGroup {
        private final int[] keys;
        private boolean ended = false;

        public OverrideGroup(int... _keys) {
            keys = _keys;
        }

        public void end() {
            ended = true;
            ControlOverrider.rebuild();
        }
    }
}