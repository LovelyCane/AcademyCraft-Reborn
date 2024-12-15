package cn.lambdalib2.auxgui;


import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fired whenever an new AuxGui is opened.
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class OpenAuxGuiEvent extends Event {
    
    public final AuxGui gui;

    public OpenAuxGuiEvent(AuxGui _gui) {
        gui = _gui;
    }

}
