package cn.academy.internal.terminal.app;

import cn.academy.internal.client.ui.auxgui.FreqTransmitterUI;
import cn.academy.internal.terminal.App;
import cn.academy.internal.terminal.AppEnvironment;
import cn.academy.internal.client.ui.auxgui.TerminalUI;
import cn.academy.internal.terminal.RegApp;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */

public class AppFreqTransmitter extends App {
    @RegApp
    public static AppFreqTransmitter instance = new AppFreqTransmitter();

    private AppFreqTransmitter() {
        super("freq_transmitter");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AppEnvironment createEnvironment() {
        return new AppEnvironment() {
            @Override
            @SideOnly(Side.CLIENT)
            public void onStart() {
                TerminalUI.passOn(new FreqTransmitterUI());
            }
        };
    }

}