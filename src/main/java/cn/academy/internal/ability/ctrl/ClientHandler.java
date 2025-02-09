package cn.academy.internal.ability.ctrl;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftConfig;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.client.ui.auxgui.CPBar;
import cn.academy.internal.client.ui.auxgui.PresetEditUI;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.datapart.CPData;
import cn.academy.internal.datapart.PresetData;
import cn.academy.internal.event.ConfigModifyEvent;
import cn.academy.internal.event.ability.FlushControlEvent;
import cn.academy.internal.event.ability.PresetSwitchEvent;
import cn.academy.internal.util.ACKeyManager;
import cn.lambdalib2.input.KeyHandler;
import cn.lambdalib2.input.KeyManager;
import cn.lambdalib2.util.GameTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Misc key event listener for skill events.
 */

@SideOnly(Side.CLIENT)
public final class ClientHandler {
    // Name constants for looking up keys in ACKeyHandler.
    public static final String KEY_SWITCH_PRESET = "switch_preset", KEY_EDIT_PRESET = "edit_preset", KEY_ACTIVATE_ABILITY = "ability_activation";

    private static final int[] keyIDsInit = new int[]{KeyManager.MOUSE_LEFT, KeyManager.MOUSE_RIGHT, Keyboard.KEY_R, Keyboard.KEY_F};

    private static final int[] keyIDs = new int[keyIDsInit.length];

    public static void init() {
        updateAbilityKeys();
    }

    private static void updateAbilityKeys() {
        AcademyCraftConfig config = AcademyCraft.academyCraftConfig;
        for (int i = 0; i < getKeyCount(); ++i) {
            keyIDs[i] = config.getKey("ability_" + i, keyIDsInit[i]);
        }

        MinecraftForge.EVENT_BUS.post(new FlushControlEvent());
    }

    public static int getKeyMapping(int id) {
        return keyIDs[id];
    }

    public static int getKeyCount() {
        return keyIDsInit.length;
    }

    /**
     * The key to activate and deactivate the ability, might have other uses in certain circumstances,
     * e.g. quit charging when using ability.
     */
    public static KeyHandler keyActivate = new KeyHandler() {

        double lastKeyDown;

        @Override
        public void onKeyUp() {
            if (Minecraft.getMinecraft().player.isSpectator())
                return;
            double delta = GameTimer.getTime() - lastKeyDown;
            if (delta < 0.300) {
                EntityPlayer player = getPlayer();
                AbilityData aData = AbilityData.get(player);

                if (aData.hasCategory()) {
                    ClientRuntime.instance().getActivateHandler().onKeyDown(player);
                }
            }

            CPBar.INSTANCE.stopDisplayNumbers();
        }

        @Override
        public void onKeyDown() {
            if (Minecraft.getMinecraft().player.isSpectator())
                return;
            lastKeyDown = GameTimer.getTime();
            CPBar.INSTANCE.startDisplayNumbers();
        }

    };

    public static KeyHandler keyEditPreset = new KeyHandler() {
        @Override
        public void onKeyDown() {
            if (AbilityData.get(getPlayer()).hasCategory()) {
                Minecraft.getMinecraft().displayGuiScreen(new PresetEditUI());
            }
        }
    };

    public static KeyHandler keySwitchPreset = new KeyHandler() {
        @Override
        public void onKeyDown() {
            PresetData data = PresetData.get(getPlayer());
            CPData cpData = CPData.get(getPlayer());

            if (cpData.isActivated()) {
                int next = (data.getCurrentID() + 1) % PresetData.MAX_PRESETS;
                data.switchFromClient(next);
                MinecraftForge.EVENT_BUS.post(new PresetSwitchEvent(data.getEntity()));
            }
        }
    };

    static {
        ACKeyManager.INSTANCE.addKeyHandler(KEY_ACTIVATE_ABILITY, Keyboard.KEY_V, keyActivate);
        ACKeyManager.INSTANCE.addKeyHandler(KEY_EDIT_PRESET, Keyboard.KEY_N, keyEditPreset);
        ACKeyManager.INSTANCE.addKeyHandler(KEY_SWITCH_PRESET, Keyboard.KEY_C, keySwitchPreset);
    }

    @SideOnly(Side.CLIENT)
    public static class ConfigHandler {
        @SubscribeEvent
        public static void onConfigModify(ConfigModifyEvent evt) {
            updateAbilityKeys();
        }
    }
}