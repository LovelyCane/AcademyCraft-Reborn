package cn.academy.internal.entity;

import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import cn.lambdalib2.util.entityx.EntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 */
public class EntityMdBall extends EntityAdvanced {
    private static final DataParameter<Integer> SPAWNER_ID = EntityDataManager.createKey(EntityMdBall.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SUB_X = EntityDataManager.createKey(EntityMdBall.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SUB_Y = EntityDataManager.createKey(EntityMdBall.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SUB_Z = EntityDataManager.createKey(EntityMdBall.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> LIFE = EntityDataManager.createKey(EntityMdBall.class, DataSerializers.VARINT);

    public static final int MAX_TETXURES = 5;

    static final float RANGE_FROM = 0.8f, RANGE_TO = 1.3f;

    //Synced states
    EntityPlayer spawner;
    float subX = 0, subY = 0, subZ = 0;

    //Ctor init data
    int life = 50;

    //Client-side data
    public int texID;

    double spawnTime;
    double lastTime;
    double burstTime = 0.4;
    public float alphaWiggle = 0.8f;
    double accel;

    public double offsetX;
    public double offsetY;
    public double offsetZ;

    public EntityMdBall(EntityPlayer player) {
        this(player, 2333333, null);
    }

    public EntityMdBall(EntityPlayer player, int life) {
        this(player, life, null);
    }

    public EntityMdBall(EntityPlayer player, int life, final EntityCallback<EntityMdBall> callback) {
        super(player.getEntityWorld());
        this.spawner = player;

        // Calc the sub-offset
        float theta = -player.rotationYaw / 180 * MathUtils.PI_F + RandUtils.rangef(-MathUtils.PI_F * 0.45f, MathUtils.PI_F * 0.45f);

        float range = RandUtils.rangef(RANGE_FROM, RANGE_TO);
        subX = MathHelper.sin(theta) * range;
        subZ = MathHelper.cos(theta) * range;

        subY = RandUtils.rangef(-1.2f, 0.2f);

        // Pos init
        updatePosition();

        this.life = life;

        this.executeAfter((EntityCallback<EntityMdBall>) Entity::setDead, life);
        if (callback != null) this.executeAfter(callback, life - 2);
    }

    public EntityMdBall(World world) {
        super(world);
        spawnTime = GameTimer.getTime();
        ignoreFrustumCheck = true; // Small variation in render tick posupdate will cause problem
    }

    @Override
    public void entityInit() {
        this.dataManager.register(SPAWNER_ID, 0);
        this.dataManager.register(SUB_X, 0F);
        this.dataManager.register(SUB_Y, 0F);
        this.dataManager.register(SUB_Z, 0F);
        this.dataManager.register(LIFE, 0);
    }

    @Override
    public void onFirstUpdate() {
        if (!world.isRemote) {
            this.dataManager.set(SPAWNER_ID, spawner.getEntityId());
            this.dataManager.set(SUB_X, subX);
            this.dataManager.set(SUB_Y, subY);
            this.dataManager.set(SUB_Z, subZ);
            this.dataManager.set(LIFE, life);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {

            if (getSpawner() == null) {
                int eid = this.dataManager.get(SPAWNER_ID);
                Entity e = world.getEntityByID(eid);
                if (e instanceof EntityPlayer) {
                    spawner = (EntityPlayer) e;
                }

            } else {
                if (subX == 0 && subY == 0 && subZ == 0) {
                    subX = this.dataManager.get(SUB_X);
                    subY = this.dataManager.get(SUB_Y);
                    subZ = this.dataManager.get(SUB_Z);
                    life = this.dataManager.get(LIFE);
                } else {
                    updatePosition();
                }
            }

        } else {

            updatePosition();

        }
    }

    public EntityPlayer getSpawner() {
        return spawner;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
    }

    @SideOnly(Side.CLIENT)
    public boolean updateRenderTick() {

        if (spawner == null || (subX == 0 && subY == 0 && subZ == 0)) return false;

        final double maxAccel = 4;
        double time = GameTimer.getTime();
        double life = time - spawnTime;

        //Alpha wiggling
        if (lastTime != 0) {
            double dt = time - lastTime;
            if (rand.nextInt(8) < 3) {
                accel = RandUtils.ranged(-maxAccel, maxAccel);
                //System.out.println("AccelChange=>" + accel);
            }

            //System.out.println("AV=>" + alphaVel);
            alphaWiggle += accel * dt;
            if (alphaWiggle > 1) alphaWiggle = 1;
            if (alphaWiggle < 0) alphaWiggle = 0;
        }
        lastTime = time;

        //Texture wiggling
        if (rand.nextInt(8) < 2) {
            texID = rand.nextInt(MAX_TETXURES);
        }

        //Surrounding
        float phase = (float) (life / 0.3f);
        offsetX = 0.03 * MathHelper.sin(phase);
        offsetZ = 0.03 * MathHelper.cos(phase);
        offsetY = 0.04 * MathHelper.cos((float) (phase * 1.4 + Math.PI / 3.5));

        updatePosition();

        return true;
    }

    public float getAlpha() {
        float lifeS = life * 0.05f;
        double time = GameTimer.getTime();
        float dt = (float) (time - spawnTime);

        final float blendTime = 0.15f;
        if (dt > lifeS - blendTime) return Math.max(0, MathUtils.lerpf(1, 0, (dt - (lifeS - blendTime)) / blendTime));
        if (dt > lifeS - burstTime)
            return MathUtils.lerpf(0.6f, 1.0f, (float) ((dt - (lifeS - burstTime)) / (burstTime - blendTime)));
        if (dt < 0.3f) return MathUtils.lerpf(0, 0.6f, dt / 0.3f);
        return 0.6f;
    }

    public float getSize() {
        int lifeMS = life * 50;
        double time = GameTimer.getTime();
        double dt = time - spawnTime;

        if (dt > lifeMS - 100) return Math.max(0, MathUtils.lerpf(1.5f, 0, (float) (dt - (lifeMS - 100)) / 100));
        if (dt > lifeMS - 300) return MathUtils.lerpf(1, 1.5f, (float) (dt - (lifeMS - 300)) / 200);
        return 1;
    }

    private void updatePosition() {
        setPosition(spawner.posX + subX, spawner.posY + subY, spawner.posZ + subZ);
    }
}