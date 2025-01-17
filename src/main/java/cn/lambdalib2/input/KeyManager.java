package cn.lambdalib2.input;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftConfig;
import cn.lambdalib2.util.ClientUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The instance of this class handles a set of KeyHandlers, and restore their key bindings
 * from a configuration. (If any)
 *
 * @author WeAthFolD
 */

@SideOnly(Side.CLIENT)
public class KeyManager {
    /**
     * The most commonly used KeyManager. Use this if you don't want to use any config on keys.
     */
    public static final KeyManager dynamic = new KeyManager();

    public static final int MOUSE_LEFT = -100;
    public static final int MOUSE_RIGHT = -99;

    public static String getKeyName(int keyid) {
        String ret;
        if (keyid >= 0) {
            ret = Keyboard.getKeyName(keyid);
        } else {
            ret = Mouse.getButtonName(keyid + 100);
        }
        return ret == null ? "undefined" : ret;
    }

    public static boolean getKeyDown(int keyID) {
        if (keyID > 0) {
            return Keyboard.isKeyDown(keyID);
        }

        return Mouse.isButtonDown(keyID + 100);
    }

    private boolean active = true;

    private final Map<String, KeyHandlerState> _bindingMap = new HashMap<>();

    public KeyManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getKeyID(KeyHandler handler) {
        KeyHandlerState kb = getKeyBinding(handler);
        return kb == null ? -1 : kb.keyID;
    }

    public void addKeyHandler(String name, int defKeyID, KeyHandler handler) {
        addKeyHandler(name, "", defKeyID, false, handler);
    }

    public void addKeyHandler(String name, String keyDesc, int defKeyID, KeyHandler handler) {
        addKeyHandler(name, keyDesc, defKeyID, false, handler);
    }

    /**
     * Add a key handler.
     *
     * @param keyDesc  Description of the key in the configuration file
     * @param defKeyID Default key ID in config file
     * @param global   If global=true, this key will have callback even if opening GUI.
     */
    public void addKeyHandler(String name, String keyDesc, int defKeyID, boolean global, KeyHandler handler) {
        if (_bindingMap.containsKey(name))
            throw new RuntimeException("Duplicate key: " + name + " of object " + handler);

        AcademyCraftConfig config = AcademyCraft.academyCraftConfig;
        int keyID = defKeyID;
        keyID = config.getKey(name, defKeyID);
        KeyHandlerState kb = new KeyHandlerState(handler, keyID, global);
        _bindingMap.put(name, kb);
    }

    /**
     * Removes a key handler from map, if exists.
     */
    public void removeKeyHandler(String name) {
        KeyHandlerState kb = _bindingMap.get(name);
        if (kb != null)
            kb.dead = true;
    }

    public void resetBindingKey(String name, int newKey) {
        KeyHandlerState kb = _bindingMap.get(name);
        if (kb != null) {
            AcademyCraftConfig config = AcademyCraft.academyCraftConfig;
            config.setKey(name, newKey);
            kb.keyID = newKey;
            if (kb.keyDown)
                kb.handler.onKeyAbort();

            kb.keyDown = false;
        }
    }

    private void tick() {
        Iterator<Entry<String, KeyHandlerState>> iter = _bindingMap.entrySet().iterator();
        boolean shouldAbort = !ClientUtils.isPlayerInGame();

        while (iter.hasNext()) {
            Entry<String, KeyHandlerState> entry = iter.next();
            KeyHandlerState kb = entry.getValue();
            if (kb.dead) {
                iter.remove();
            } else {
                boolean down = getKeyDown(kb.keyID);
                if (kb.keyDown && shouldAbort) {
                    kb.keyDown = false;
                    kb.keyAborted = true;
                    kb.handler.onKeyAbort();
                } else if (!kb.keyDown && down && !shouldAbort && !kb.keyAborted) {
                    kb.keyDown = true;
                    kb.handler.onKeyDown();
                } else if (kb.keyDown && !down) {
                    kb.keyDown = false;
                    kb.handler.onKeyUp();
                } else if (kb.keyDown) {
                    kb.handler.onKeyTick();
                }

                if (!down) {
                    kb.keyAborted = false;
                }

                kb.keyDown = down;
            }
        }
    }

    private KeyHandlerState getKeyBinding(KeyHandler handler) {
        for (KeyHandlerState kb : _bindingMap.values()) {
            if (kb.handler == handler)
                return kb;
        }
        return null;
    }

    @SubscribeEvent
    public void _onEvent(ClientTickEvent event) {
        if (event.phase == Phase.START && active) {
            tick();
        }
    }

    private static class KeyHandlerState {
        KeyHandler handler;
        boolean isGlobal;

        int keyID;

        boolean keyDown;
        boolean keyAborted;

        boolean dead;

        private KeyHandlerState(KeyHandler h, int k, boolean g) {
            handler = h;
            keyID = k;
            isGlobal = g;
        }
    }
}
