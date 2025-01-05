package cn.academy.internel.terminal.app;

import cn.academy.internel.auxgui.FreqTransmitterUI;
import cn.academy.internel.terminal.App;
import cn.academy.internel.terminal.AppEnvironment;
import cn.academy.internel.auxgui.TerminalUI;
import cn.academy.internel.terminal.RegApp;
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