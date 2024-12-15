/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.s11n.network;


import cn.lambdalib2.LambdaLib2;
import cn.lambdalib2.s11n.network.NetworkS11n.ContextException;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * This class implements a simple event bus based on network s11n. You can send out network-serializable objects
 * as events. each event class is associated with a series of event handlers on each side.
 */

public class NetworkEvent {

    private static final SimpleNetworkWrapper channel = LambdaLib2.channel;
    private static Map<Class<?>, List/*<INetEventHandler<?>>*/> // here type induction isn't helpful
            handlerClient = new HashMap<>(),
            handlerServer = new HashMap<>();

    public static void sendToServer(Object msg) {
        channel.sendToServer(new Message(msg));
    }

    public static void sendTo(Object msg, EntityPlayerMP player) {
        channel.sendTo(new Message(msg), player);
    }

    public static void sendToAll(Object msg) {
        channel.sendToAll(new Message(msg));
    }

    public static void sendToAllAround(Object msg, TargetPoint trg) {
        channel.sendToAllAround(new Message(msg), trg);
    }

    public static void sendToDimension(Object msg, int dimensionId) {
        channel.sendToDimension(new Message(msg), dimensionId);
    }

    /**
     * Adds a network event handler on both sides.
     */
    public static <T> void listen(Class<T> type, INetEventHandler<T> handler) {
        listen(type, Side.CLIENT, handler);
        listen(type, Side.SERVER, handler);
    }

    /**
     * Adds a network event handler on given side.
     */
    public static <T> void listen(Class<T> type, Side side, INetEventHandler<T> handler) {
        _handlerList(type, side, true).add(handler);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<INetEventHandler<T>> _handlerList(Class<T> type, Side side, boolean create) {
        boolean client = side == Side.CLIENT;
        Map<Class<?>, List> map = client ? handlerClient : handlerServer;
        List<INetEventHandler<T>> ret = map.get(type);

        if (create && ret == null) {
            ret = new ArrayList<>();
            map.put(type, ret);
        }

        return ret == null ? Collections.emptyList() : ret;
    }

    public interface INetEventHandler<T> {
        void onEvent(T event, MessageContext ctx);

        @SideOnly(Side.CLIENT)
        default EntityPlayer getClientPlayer() {
            return Minecraft.getMinecraft().player;
        }
    }

    public static final class Message implements IMessage {

        public Object object = null;

        public Message(Object _obj) {
            object = _obj;
        }

        public Message() {
        }

        public void fromBytes(ByteBuf buf) {
            try {
                object = NetworkS11n.deserialize(buf);
            } catch (ContextException exc) {
                // omit
            }
        }

        public void toBytes(ByteBuf buf) {
            NetworkS11n.serialize(buf, object, false);
        }

    }

    public static final class MessageHandler implements IMessageHandler<Message, IMessage> {

        @SuppressWarnings("unchecked")
        public IMessage onMessage(Message msg, MessageContext ctx) {
            if (msg.object != null) {
                _handlerList(msg.object.getClass(), ctx.side, false)
                        .forEach(handler -> ((INetEventHandler) handler).onEvent(msg.object, ctx));
            } // else { silently omit the message }

            return null;
        }

    }


}
