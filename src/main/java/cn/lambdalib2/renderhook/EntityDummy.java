package cn.lambdalib2.renderhook;

import cn.lambdalib2.registry.mc.RegEntity;
import cn.lambdalib2.util.ViewOptimize.IAssociatePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntityDummy extends Entity implements IAssociatePlayer {
    
    AbstractClientPlayer player;
    final DummyRenderData data;
    
    boolean set;
    float lastRotationYaw, lastRotationYawHead, rotationYawHead;
    float lastRotationPitch;
    
    public EntityDummy(DummyRenderData _data) {
        super(_data.getEntity().world);
        data = _data;
        player = (AbstractClientPlayer) _data.getEntity();
        forceSpawn = true;
        ignoreFrustumCheck = true;

        setPosition(player.posX, player.posY, player.posZ);
    }

    @Override
    protected void entityInit() {
    }
    
    @Override
    public void onUpdate() {
        if(!set) {
            set = true;
            lastRotationYaw = player.renderYawOffset;
            lastRotationYawHead = player.rotationYawHead;
            lastRotationPitch = player.rotationPitch;
        } else {
            lastRotationYaw = rotationYaw;
            lastRotationYawHead = rotationYawHead;
            lastRotationPitch = rotationPitch;
        }
        
        posX = player.posX;
        posY = player.posY;
        posZ = player.posZ;
        
        rotationYaw = player.renderYawOffset;
        rotationYawHead = player.rotationYawHead;
        rotationPitch = player.rotationPitch;
    }
    
    @Override
    public EntityPlayer getPlayer() {
        return player;
    }
    
    /**
     * TODO: Support all passes
     */
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound t) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound t) {}

}
