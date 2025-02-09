package cn.academy.internal.util;

import cn.academy.internal.client.ui.auxgui.TerminalUI;
import cn.academy.internal.event.ConfigModifyEvent;
import cn.academy.internal.terminal.app.settings.SettingsUI;
import cn.lambdalib2.input.KeyHandler;
import cn.lambdalib2.input.KeyManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ACKeyManager extends KeyManager {
    public static final KeyManager INSTANCE = new ACKeyManager();

    static {
        ACKeyManager.INSTANCE.addKeyHandler("open_data_terminal", Keyboard.KEY_LMENU, TerminalUI.keyHandler);
    }

    private ACKeyManager() {
    }

    @SubscribeEvent
    public void onConfigModified(ConfigModifyEvent event) {
        resetBindingKey(event.name, event.value);
    }

    @Override
    public void addKeyHandler(String name, String keyDesc, int defKeyID, boolean global, KeyHandler handler) {
        super.addKeyHandler(name, keyDesc, defKeyID, global, handler);
        SettingsUI.addKey( name, defKeyID);
    }
}