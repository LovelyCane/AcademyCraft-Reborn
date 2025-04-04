package cn.academy.internal.event.energy;

import cn.academy.internal.energy.api.block.IWirelessTile;
import cn.academy.internal.event.WirelessUserEvent;

/**
 * Fired whenever a wireless user is to be destroyed.</br>
 * NOTE: When a user is no longer available, it will be automatically unlinked.
 * @author WeathFolD
 */
public class UnlinkUserEvent extends WirelessUserEvent {
    public UnlinkUserEvent(IWirelessTile _tile) {
        super(_tile);
    }
}