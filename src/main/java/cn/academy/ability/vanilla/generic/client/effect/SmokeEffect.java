package cn.academy.ability.vanilla.generic.client.effect;

import cn.academy.entity.LocalEntity;
import cn.lambdalib2.util.GameTimer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SmokeEffect extends LocalEntity {
    public final double initTime;
    public float rotation = 0.0f;
    public float size = 1.0f;
    public final float lifeModifier;
    public final float rotSpeed;
    public final int frame;

    public SmokeEffect(World world) {
        super(world);
        setSize(1, 1);

        this.initTime = getTime();
        this.lifeModifier = 0.5f + rand.nextFloat() * 0.2f;
        this.rotSpeed = 0.3f * (rand.nextFloat() + 3);
        this.frame = rand.nextInt(4);
    }

    @Override
    public void onUpdate() {
        rotation += rotSpeed;

        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        if (deltaTime() >= 4f) {
            setDead();
        }
    }

    public float alpha() {
        float dt = (float) (deltaTime() / lifeModifier);
        if (dt <= 0.3f) {
            return dt / 0.3f;
        } else if (dt <= 1.5f) {
            return 1.0f;
        } else if (dt <= 2.0f) {
            return 1 - (dt - 1.5f) / 0.5f;
        } else {
            return 0.0f;
        }
    }

    private double deltaTime() {
        return getTime() - initTime;
    }

    private double getTime() {
        return GameTimer.getTime();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}