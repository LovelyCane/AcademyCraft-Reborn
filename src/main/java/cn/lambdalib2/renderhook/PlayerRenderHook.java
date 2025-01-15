package cn.lambdalib2.renderhook;

import cn.lambdalib2.util.GameTimer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Register through DummyRenderData.get(player).addRenderHook(hook)
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public abstract class PlayerRenderHook {
    EntityPlayer player;
    boolean disposed;
    double createTime = GameTimer.getTime();
    
    public void renderHand(boolean firstPerson) {}
    
    public void dispose() {
        disposed = true;
    }
    
    public final EntityPlayer getPlayer() {
        return player;
    }
    
    protected double getElapsedTime() {
        return GameTimer.getTime() - createTime;
    }
}
