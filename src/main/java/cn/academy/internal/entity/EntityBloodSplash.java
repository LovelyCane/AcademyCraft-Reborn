package cn.academy.internal.entity;

import cn.academy.Resources;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntityBloodSplash extends EntityAdvanced
{

    public static ResourceLocation[] SPLASH = Resources.getEffectSeq("blood_splash", 10);

    public int frame;

    public EntityBloodSplash(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(RandUtils.rangef(0.8f, 1.3f));
    }

    @Override
    protected void entityInit() {

    }

    public void setSize(float size) {
        this.width = this.height = size;
    }

    public float getSize() {
        return this.width;
    }

    @Override
    public void onUpdate() {
        if (++frame == SPLASH.length) {
            setDead();
        }
        super.onUpdate();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}