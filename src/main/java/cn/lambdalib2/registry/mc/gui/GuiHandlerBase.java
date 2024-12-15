package cn.lambdalib2.registry.mc.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GuiHandlerBase {

    private Object mod;
    private int guiId;

    /*
     * API for registry part.
     */

    void init(Object mod, int guiId) {
        this.mod = mod;
        this.guiId = guiId;
    }

    /*
     * API for external call.
     */

    /**
     * Open a gui container. Should be called on SERVER ONLY.
     * Side check is enforced.
     */
    public final void openGuiContainer(EntityPlayer player, World world, int x, int y, int z) {
        if (!world.isRemote)
            player.openGui(mod, guiId, world, x, y, z);
    }

    /*
     * GUI/Container generation
     */

    @SideOnly(Side.CLIENT)
    protected abstract Object getClientContainer(EntityPlayer player, World world, int x, int y, int z);

    protected abstract Object getServerContainer(EntityPlayer player, World world, int x, int y, int z);
}
