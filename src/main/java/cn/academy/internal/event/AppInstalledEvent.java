package cn.academy.internal.event;

import cn.academy.internal.terminal.App;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;


public class AppInstalledEvent extends Event {
    public final EntityPlayer player;
    public final App app;

    public AppInstalledEvent(EntityPlayer _player, App _app) {
        player = _player;
        app = _app;
    }
}