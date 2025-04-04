package cn.academy.internal.entity;

import cn.academy.internal.client.renderer.util.ArcFactory;
import cn.academy.internal.client.renderer.util.ArcFactory.Arc;
import cn.academy.internal.client.renderer.util.CubePointFactory;
import cn.academy.internal.client.renderer.util.IPointFactory;
import cn.academy.internal.client.renderer.util.SubArcHandler;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Spawn a surround arc effect around the specific entity or block.
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class EntitySurroundArc extends EntityAdvanced {
    
    static final int TEMPLATES = 15;
    
    static {
        ArcFactory factory = new ArcFactory();
        factory.widthShrink = 0.9;
        factory.maxOffset = 0.8;
        factory.passes = 3;
        factory.width = 0.2;
        factory.branchFactor = 0.7;
        
        ArcType.THIN.templates = factory.generateList(10, 1.5, 2);
        
        factory.width = 0.3;
        ArcType.NORMAL.templates = factory.generateList(10, 3, 4);
        
        factory.passes = 3;
        factory.width = 0.35;
        factory.maxOffset =    1.2;
        factory.branchFactor = 0.45;
        ArcType.BOLD.templates = factory.generateList(10, 3.5, 4.5);
    }
    
    public enum ArcType {
        THIN(4), NORMAL(6), BOLD(5);
        
        public Arc[] templates;
        public int count;
        
        ArcType(int _count) {
            count = _count;
        }
    }

    private ArcType arcType = ArcType.BOLD;
    private final PosObject pos;
    
    public boolean draw = true;
    
    public int life = 100;

    public SubArcHandler arcHandler;
    
    IPointFactory pointFactory;
    
    public EntitySurroundArc(Entity follow) {
        this(follow, 1.3);
    }
    
    public EntitySurroundArc(Entity follow, double sizeMultiplyer) {
        super(follow.world);
        pos = new EntityPos(follow);
        setPosition(follow.posX, follow.posY, follow.posZ);
        pointFactory = new CubePointFactory(
            follow.width * sizeMultiplyer, 
            follow.height * sizeMultiplyer, 
            follow.width * sizeMultiplyer).setCentered(true);
    }
    
    public EntitySurroundArc(World world, double x, double y, double z, double wl, double h) {
        super(world);
        posX = x;
        posY = y;
        posZ = z;
        pos = new ConstPos(x, y, z);
        pointFactory = new CubePointFactory(wl, h, wl).setCentered(true);
    }
    
    public void updatePos(double x, double y, double z) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
    }
    
    public EntitySurroundArc setArcType(ArcType type) {
        arcType = type;
        return this;
    }
    
    public EntitySurroundArc setLife(int life) {
        this.life = life;
        return this;
    }
    
    @Override
    public void entityInit() {
        ignoreFrustumCheck = true;
    }
    
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
    
    @Override
    public void onFirstUpdate() {
        // Create the arcs!
        arcHandler = new SubArcHandler(arcType.templates);
        arcHandler.frameRate = 0.6;
        arcHandler.switchRate = 0.7;
        
        doGenerate();
    }
    
    /**
     * Do the arc generation.
     */
    protected void doGenerate() {
        for(int i = 0; i < arcType.count; ++i) {
            double yaw = rand.nextDouble() * Math.PI * 2;
            double pitch = rand.nextDouble() * Math.PI;
            
            double y = Math.sin(pitch),
                zz = Math.sqrt(1 - y * y),
                x = zz * Math.sin(yaw),
                z = zz * Math.cos(yaw);
            
            arcHandler.generateAt(pointFactory.next());
        }
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if(arcHandler.isEmpty())
            doGenerate();
        
        arcHandler.tick();
        
        pos.tick();
        setPosition(pos.x, pos.y, pos.z);
        rotationYaw = pos.yaw;
        rotationPitch = pos.pitch;
        
        if(ticksExisted == life)
            setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}
    
    private abstract class PosObject {
        double x, y, z;
        float yaw, pitch;
        
        void tick() {}
    }

    private class EntityPos extends PosObject {
        
        final Entity entity;
        final boolean isPlayer;
        
        public EntityPos(Entity e) {
            entity = e;
            isPlayer = e instanceof EntityPlayer && e.equals(Minecraft.getMinecraft().player);
        }
        
        @Override
        void tick() {
            x = entity.posX;
            y = entity.posY;
            z = entity.posZ;
            yaw = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).rotationYawHead : entity.rotationYaw;
            pitch = entity.rotationPitch;
        }
    }
    
    private class ConstPos extends PosObject {
        
        public ConstPos(double _x, double _y, double _z) {
            x = _x;
            y = _y;
            z = _z;
        }
    }

}