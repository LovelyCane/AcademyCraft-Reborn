package cn.lambdalib2.renderhook;

import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
@RegDataPart(value=EntityPlayer.class, side=Side.CLIENT)
public class DummyRenderData extends DataPart<EntityPlayer> {

    public DummyRenderData() {
        setTick(true);
    }
    
    public static DummyRenderData get(EntityPlayer p) {
        return EntityData.get(p).getPart(DummyRenderData.class);
    }
    
    private EntityDummy entity;
    public List<PlayerRenderHook> renderers = new LinkedList<>();
    
    @Override
    public void tick() {
        if(entity != null) {
            entity.player = (AbstractClientPlayer) getEntity();
        }

        renderers.removeIf(val -> val.disposed);
        
        // Destroy the entity when no more needed, saving resources
        if(entity != null && renderers.isEmpty()) {
            entity.setDead();
            entity = null;
        }
    }
    
    @Override
    public void fromNBT(NBTTagCompound tag) {
        // N/A
    }
    
    public void addRenderHook(PlayerRenderHook hook) {
        EntityPlayer player = getEntity();
        hook.player = player;
        hook.disposed = false;
        
        if(entity == null || entity.isDead) {
            player.world.spawnEntity(
                entity = new EntityDummy(this));
        }
        
        renderers.add(hook);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
    }
}
