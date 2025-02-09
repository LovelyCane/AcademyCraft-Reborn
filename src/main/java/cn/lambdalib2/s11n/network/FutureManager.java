package cn.lambdalib2.s11n.network;

import cn.lambdalib2.util.SideUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Consumer;

@NetworkS11nType
public enum FutureManager {
    instance;

    private static final String MSG_RESULT = "result";

    final ThreadLocal<Context> threadContext = ThreadLocal.withInitial(Context::new);

    public void init() {
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

    @NetworkMessage.Listener(channel = MSG_RESULT, side = {Side.CLIENT, Side.SERVER})
    private <T> void hReceiveResult(int increm, @NetworkMessage.NullablePar T value) {
        Context ctx = threadContext.get();

        Future future = ctx.waitingFutures.get(increm);
        if (future != null) {
            future.callback.accept(value);
            ctx.waitingFutures.remove(future.increm);
        }
    }

    @SubscribeEvent
    public void __onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
        disconnect();
    }

    @SubscribeEvent
    public void __onServerDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent evt) {
        disconnect();
    }

    private void disconnect() {
        threadContext.get().waitingFutures.clear();
    }
}
