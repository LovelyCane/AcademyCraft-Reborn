package cn.academy.internal.event;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired whenever a specific property in AC config has been edited.
 * @author WeAthFolD
 */
public class ConfigModifyEvent extends Event {
    // key name
    public final String name;
    // key value
    public final int value;
    
    public ConfigModifyEvent(String name,int value) {
        this.name = name;
        this.value = value;
    }
}