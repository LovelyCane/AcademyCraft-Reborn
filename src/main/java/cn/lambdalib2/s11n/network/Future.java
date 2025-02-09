package cn.lambdalib2.s11n.network;

import cn.lambdalib2.s11n.network.NetworkS11n.ContextException;
import cn.lambdalib2.s11n.network.NetworkS11n.NetS11nAdaptor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * {@link Future} is a object that receives a callback when its value becomes available. This Future specially designed
 * for NetworkS11n. It behaves as following: <br>
 * <ul>
 * <li>A future object is created with {@link #create(Consumer)} at one side.</li>
 * <li>This future object can be network-serialized and re-created in another side.</li>
 * <li>When called {@link #sendResult(T)}, the callback provided in creation stage is invoked at creation side.
 * If you call the method at creation side, the behaviour is undefined.</li>
 * <li>Once a callback is received, the Future is rendered useless and will receive no further results.</li>
 * </ul>
 */

public class Future<T> {

    int increm;
    Consumer<T> callback; // Valid only on creation side
    EntityPlayer creator; // Valid only if created in client

    public static <T> Future<T> create(Consumer<T> callback) {
        return FutureManager.instance.create(callback);
    }

    public void sendResult(T value) {
        FutureManager.instance.sendResult(this, value);
    }

    Side getSide() {
        return creator == null ? Side.SERVER : Side.CLIENT;
    }

}

class S11nHandler implements NetS11nAdaptor<Future> {

    @Override
    public void write(ByteBuf buf, Future obj) {
        buf.writeInt(obj.increm);
        NetworkS11n.serialize(buf, obj.creator, true);
    }

    @Override
    public Future read(ByteBuf buf) throws ContextException {
        int increm = buf.readInt();
        EntityPlayer player = NetworkS11n.deserialize(buf);

        Future ret = new Future();
        ret.increm = increm;
        ret.creator = player;

        return ret;
    }

}

class Context {
    int increm;
    Map<Integer, Future> waitingFutures = new HashMap<>();
}
