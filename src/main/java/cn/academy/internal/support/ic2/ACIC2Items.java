package cn.academy.internal.support.ic2;

import net.minecraft.item.Item;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import cn.lambdalib2.registry.RegistryCallback;

import net.minecraftforge.fml.common.Optional;

/**
 * Automatically generated by LambdaLib2.xconf in 2019-02-06 21:37:03.
 */
@Optional.Interface(modid = IC2Support.IC2_MODID, iface = IC2Support.IC2_IFACE)
public class ACIC2Items {


    @RegistryCallback
    @SuppressWarnings("sideonly")
    private static void registerItems(RegistryEvent.Register<Item> event) {

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderers() {
    }

}