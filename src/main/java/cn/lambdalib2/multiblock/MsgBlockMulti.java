package cn.lambdalib2.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeathFolD
 *
 */
public class MsgBlockMulti implements IMessage {

    BlockPos pos;
    EnumFacing dir;
    int s; // subID

    public MsgBlockMulti(InfoBlockMulti i) {
        TileEntity te = i.te;
        pos = te.getPos();
        dir = i.dir;
        s = i.subID;
    }

    public MsgBlockMulti() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        dir = EnumFacing.values()[buf.readByte()];
        s = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ()).writeByte(dir.ordinal()).writeByte(s);
    }

    public static class Req implements IMessage {

        BlockPos pos;

        public Req(InfoBlockMulti ibm) {
            TileEntity te = ibm.te;
            pos = te.getPos();
        }

        public Req() {
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ());
        }

    }

    public static class ReqHandler implements IMessageHandler<Req, MsgBlockMulti> {

        @Override
        public MsgBlockMulti onMessage(Req msg, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.world.getTileEntity(msg.pos);
            if (te instanceof IMultiTile) {
                InfoBlockMulti i = ((IMultiTile) te).getBlockInfo();
                if (i != null) {
                    return new MsgBlockMulti(i);
                }
            }
            return null;
        }

    }

    public static class Handler implements IMessageHandler<MsgBlockMulti, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MsgBlockMulti msg, MessageContext ctx) {
            World world = Minecraft.getMinecraft().world;
            TileEntity te = world.getTileEntity(msg.pos);
            if (!(te instanceof IMultiTile))
                return null;
            InfoBlockMulti info = ((IMultiTile) te).getBlockInfo();
            // If TE is there and info is present, do the sync.
            if (info != null) {
                info.dir = msg.dir;
                info.subID = msg.s;
                info.setLoaded();
            }
            return null;
        }

    }

}
