package cn.lambdalib2.s11n.network;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkMessage.NullablePar;
import cn.lambdalib2.s11n.network.NetworkS11n.ContextException;
import cn.lambdalib2.s11n.network.NetworkS11n.NetS11nAdaptor;
import cn.lambdalib2.util.SideUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;
import scala.Function1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NetworkS11nType
enum FutureManager {
    instance;

    private static final String MSG_RESULT = "result";

    ThreadLocal<Context> threadContext = ThreadLocal.withInitial(Context::new);

    void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    <T> Future<T> create(Consumer<T> callback) {
        Context ctx = threadContext.get();

        ++ctx.increm;

        Future<T> fut = new Future<>();
        fut.increm = ctx.increm;
        fut.callback = callback;
        fut.creator = SideUtils.getThePlayer(); // null if in server, thePlayer if in client

        ctx.waitingFutures.put(ctx.increm, fut);

        return fut;
    }

    <T> void sendResult(Future<T> fut, T value) {
        if (fut.getSide() == SideUtils.getRuntimeSide()) {
            throw new IllegalStateException("Trying to sendResult in creation side of Future");
        }

        if (SideUtils.isClient()) {
            NetworkMessage.sendToServer(instance, MSG_RESULT, fut.increm, value);
        } else {
            NetworkMessage.sendTo(fut.creator, instance, MSG_RESULT, fut.increm, value);
        }
    }

    @Listener(channel = MSG_RESULT, side = {Side.CLIENT, Side.SERVER})
    private <T> void hReceiveResult(int increm, @NullablePar T value) {
        Context ctx = threadContext.get();

        Future future = ctx.waitingFutures.get(increm);
        if (future != null) {
            future.callback.accept(value);
            ctx.waitingFutures.remove(future.increm);
        }
    }

    @SubscribeEvent
    public void __onClientDisconnect(ClientDisconnectionFromServerEvent evt) {
        disconnect();
    }

    @SubscribeEvent
    public void __onServerDisconnect(ServerDisconnectionFromClientEvent evt) {
        disconnect();
    }

    private void disconnect() {
        threadContext.get().waitingFutures.clear();
    }
}

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

    @StateEventCallback
    private static void init(FMLInitializationEvent event) {
        NetworkS11n.addDirect(Future.class, new S11nHandler());
        FutureManager.instance.init();
    }

    public static <T> Future<T> create(Consumer<T> callback) {
        return FutureManager.instance.create(callback);
    }

    public static <T> Future<T> create2(Function1<T, ?> callback) {
        return create(new Consumer<T>() {
            @Override
            public void accept(T t) {
                callback.apply(t);
            }
        });
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
