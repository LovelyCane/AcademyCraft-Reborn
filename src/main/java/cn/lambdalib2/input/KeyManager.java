package cn.lambdalib2.input;

import cn.lambdalib2.util.ClientUtils;
import cn.lambdalib2.util.Debug;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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

    public static final int
            MOUSE_LEFT = -100, MOUSE_MIDDLE = -98, MOUSE_RIGHT = -99,
            MWHEELDOWN = -50, MWHEELUP = -49;

    public static String getKeyName(int keyid) {
        if (keyid >= 0) {
            String ret = Keyboard.getKeyName(keyid);
            return ret == null ? "undefined" : ret;
        } else {
            String ret = Mouse.getButtonName(keyid + 100);
            return ret == null ? "undefined" : ret;
        }
    }

    public static boolean getKeyDown(int keyID) {
        if (keyID > 0) {
            return Keyboard.isKeyDown(keyID);
        }

        return Mouse.isButtonDown(keyID + 100);
    }


    private boolean active = true;

    private int _anonymousHandlerCount = 0;

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

    public void addKeyHandler(int keyID, KeyHandler handler) {
        addKeyHandler(keyID, false, handler);
    }

    public void addKeyHandler(int keyID, boolean global, KeyHandler handler) {
        Debug.require(getConfig() == null, "You can't use anonymous key handlers on KeyHandler with config!");

        String name = "_anonymous_" + _anonymousHandlerCount;
        addKeyHandler(name, "", keyID, global, handler);
        ++_anonymousHandlerCount;
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

        Configuration conf = getConfig();
        int keyID = defKeyID;
        if (conf != null) {
            keyID = conf.getInt(name, "keys", defKeyID, -1000, 1000, keyDesc);
        }
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
            Configuration cfg = getConfig();
            if (cfg != null) {
                Property p = cfg.get("keys", name, kb.keyID);
                p.set(newKey);
            }

            kb.keyID = newKey;
            if (kb.keyDown)
                kb.handler.onKeyAbort();

            kb.keyDown = false;
        }
    }

    protected Configuration getConfig() {
        return null;
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
                } else if (kb.keyDown && !down && !shouldAbort) {
                    kb.keyDown = false;
                    kb.handler.onKeyUp();
                } else if (kb.keyDown && down && !shouldAbort) {
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

    private class KeyHandlerState {
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
