package cn.lambdalib2.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class SideUtils {

    // API
    public static Side getRuntimeSide() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }

    public static boolean isClient() {
        return getRuntimeSide().isClient();
    }

    public static World getWorld(int dimension) {
        return threadProxy.get().proxy.getWorld(dimension);
    }

    public static EntityPlayer getThePlayer() {
        return threadProxy.get().proxy.getThePlayer();
    }

    public static EntityPlayer findPlayerOnServer(String name) {
        return threadProxy.get().proxy.getPlayerOnServer(name);
    }

    public static EntityPlayer[] getPlayerListOnServer() {
        return threadProxy.get().proxy.getPlayerList();
    }


    // IMPL

    private static ThreadLocal<SideUtils> threadProxy = new ThreadLocal<SideUtils>() {
        @Override
        @SuppressWarnings("sideonly")
        protected SideUtils initialValue() {
            Side s = FMLCommonHandler.instance().getEffectiveSide();
            if (s.isClient()) {
                return new SideUtils(getClientProxy());
            } else {
                return new SideUtils(new ServerProxy());
            }
        }

        @SideOnly(Side.CLIENT)
        private ServerProxy getClientProxy() {
            return new ClientProxy();
        }
    };

    private final ServerProxy proxy;

    private SideUtils(ServerProxy proxy) {
        this.proxy = proxy;
    }

    private static class ServerProxy {

        public World getWorld(int dimension) {
            return DimensionManager.getWorld(dimension);
        }

        public EntityPlayer getThePlayer() {
            return null;
        }

        public EntityPlayer getPlayerOnServer(String name) {
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
        }

        public EntityPlayer[] getPlayerList() {
            List<? extends EntityPlayer> list = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
            return list.toArray(new EntityPlayer[0]);
        }

    }

    @SideOnly(Side.CLIENT)
    private static class ClientProxy extends ServerProxy {

        @Override
        public World getWorld(int dimension) {
            World theWorld = Minecraft.getMinecraft().world;
            if (theWorld != null && theWorld.provider.getDimension() == dimension) {
                return theWorld;
            } else {
                return null;
            }
        }

        @Override
        public EntityPlayer getThePlayer() {
            return Minecraft.getMinecraft().player;
        }

        @Override
        public EntityPlayer getPlayerOnServer(String name) {
            return null;
        }

        @Override
        public EntityPlayer[] getPlayerList() {
            return new EntityPlayer[] {};
        }
    }
}
