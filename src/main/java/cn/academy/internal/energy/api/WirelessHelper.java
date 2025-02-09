package cn.academy.internal.energy.api;

import cn.academy.internal.energy.api.block.IWirelessGenerator;
import cn.academy.internal.energy.api.block.IWirelessNode;
import cn.academy.internal.energy.api.block.IWirelessUser;
import cn.academy.internal.energy.impl.NodeConn;
import cn.academy.internal.energy.impl.WiWorldData;
import cn.academy.internal.energy.impl.WirelessNet;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * All kinds of funcs about wireless system.
 *
 * @author WeAthFolD
 */

public class WirelessHelper {
    public static WirelessNet getWirelessNet(IWirelessNode node) {
        TileEntity tile = (TileEntity) node;
        return WiWorldData.get(tile.getWorld()).getNetwork(node);
    }


    //-----Node Connection
    public static NodeConn getNodeConn(IWirelessNode node) {
        TileEntity tile = (TileEntity) node;
        return WiWorldData.get(tile.getWorld()).getNodeConnection(node);
    }

    public static NodeConn getNodeConn(IWirelessUser gen) {
        TileEntity tile = (TileEntity) gen;
        return WiWorldData.get(tile.getWorld()).getNodeConnection(gen);
    }

    public static boolean isGeneratorLinked(IWirelessGenerator gen) {
        return getNodeConn(gen) != null;
    }

    private static final List<BlockPos> _blockPosBuffer = new ArrayList<>();

    /**
     * Get a list of IWirelessNode that are linkable and can reach the given position.
     *
     * @return nodes in the area, does not guarantee any order
     */
    public static List<IWirelessNode> getNodesInRange(World world, BlockPos pos) {
        double range = 20.0;
        WorldUtils.getBlocksWithin(_blockPosBuffer, world, pos.getX(), pos.getY(), pos.getZ(), range, 100, (world1, x2, y2, z2, block) -> {
            TileEntity te = world1.getTileEntity(new BlockPos(x2, y2, z2));
            if (te instanceof IWirelessNode) {
                IWirelessNode node = ((IWirelessNode) te);
                NodeConn conn = getNodeConn((IWirelessNode) te);

                double distSq = MathUtils.distanceSq(pos.getX(), pos.getY(), pos.getZ(), x2, y2, z2);
                double range1 = node.getRange();

                return range1 * range1 >= distSq && conn.getLoad() < conn.getCapacity();
            } else {
                return false;
            }
        });

        List<IWirelessNode> ret = new ArrayList<>();
        for (BlockPos bp : _blockPosBuffer) {
            ret.add((IWirelessNode) world.getTileEntity(bp));
        }

        return ret;
    }
}