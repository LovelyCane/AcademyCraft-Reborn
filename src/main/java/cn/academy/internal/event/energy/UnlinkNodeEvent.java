package cn.academy.internal.event.energy;

import cn.academy.internal.energy.api.block.IWirelessNode;
import cn.academy.internal.energy.api.block.IWirelessTile;
import cn.academy.internal.event.WirelessEvent;

/**
 * Fired when a node is to be unlinked.
 * @author WeathFolD
 */
public class UnlinkNodeEvent extends WirelessEvent {
    public final IWirelessNode node;

    public UnlinkNodeEvent(IWirelessTile _node) {
        super(_node);
        node = (IWirelessNode) _node;
    }
}