package cn.academy.internal.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author WeAthFolD
 */
public class MatterUnitHarvestEvent extends Event {
    public final EntityPlayer player;

    public MatterUnitHarvestEvent(EntityPlayer _player) {
        player = _player;
    }
}