package cn.lambdalib2.util;

import cn.lambdalib2.auxgui.AuxGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Client-Side judgement helper and other stuffs.
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class ClientUtils {
    
    /**
     * @return whether the player is playing the client game and isn't opening any GUI.
     */
    public static boolean isPlayerInGame() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        return player != null && Minecraft.getMinecraft().currentScreen == null && !AuxGuiHandler.hasForegroundGui();
    }

    public static boolean isInWorld() {
        return Minecraft.getMinecraft().player != null;
    }
    
    public static boolean isPlayerPlaying() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.player != null && !mc.isGamePaused();
    }
    
    /**
     * Quick alias for playing static sound
     * @param src
     * @param pitch
     */
    public static void playSound(ResourceLocation src, float pitch) {
        Minecraft.getMinecraft().getSoundHandler().playSound(
            PositionedSoundRecord.getMasterRecord(new SoundEvent(src), pitch));
    }

    public static String getClipboardContent() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            if(cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                return (String) cb.getData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setClipboardContent(String content) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(content);
        try {
            cb.setContents(ss, ss);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

}
