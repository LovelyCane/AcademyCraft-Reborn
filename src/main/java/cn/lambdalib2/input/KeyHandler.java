package cn.lambdalib2.input;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyHandler {

    public void onKeyDown() {}
    
    public void onKeyUp() {}
    
    /**
     * This happens when the KeyBinding is a non-global one, 
     * and player opens any GUI or jumps out of the game.
     */
    public void onKeyAbort() {}
    
    public void onKeyTick() {}
    
    @SideOnly(Side.CLIENT)
    protected Minecraft getMC() {
        return Minecraft.getMinecraft();
    }
    
    @SideOnly(Side.CLIENT)
    protected EntityPlayer getPlayer() {
        return getMC().player;
    }
    
}
