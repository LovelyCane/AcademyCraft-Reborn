package cn.academy.block;

import cn.academy.client.render.block.RenderDynamicBlock;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AbilityInterf {
    public static final double minRange = 10.0;
    public static final double maxRange = 100.0;

    public static final String MSG_SYNC = "sync";
    public static final String MSG_UPDATE_RANGE = "set_range";
    public static final String MSG_UPDATE_WHITELIST = "set_whitelist";
    public static final String MSG_UPDATE_ENABLED = "set_enabled";
    public static final int SLOT_BATTERY = 0;

    @SideOnly(Side.CLIENT)
    @StateEventCallback
    public static void regClient(FMLInitializationEvent ev) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileAbilityInterferer.class, new RenderDynamicBlock());
    }
}
