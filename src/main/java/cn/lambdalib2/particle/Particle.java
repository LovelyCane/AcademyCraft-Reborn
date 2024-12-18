package cn.lambdalib2.particle;

import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import cn.lambdalib2.util.entityx.handlers.Rigidbody;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Color;

/**
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public final class Particle extends EntityAdvanced implements ISpriteEntity {

    public ResourceLocation texture = null;
    public Color color = Colors.white();
    public float size = 1.0f;
    public boolean hasLight = false;
    public double gravity = 0.0;
    public boolean needRigidbody = true;
    /**
     * When set to true this particle is rotated with rotationYaw and
     * rotationPitch, else always faces the player.
     */
    public boolean customRotation = false;

    double creationTime;

    public int fadeInTime = 5;
    public int fadeTime;
    public int life = 10000000;
    float startAlpha;

    boolean updated;

    public Particle() {
        super(null);
    }

    public void fadeAfter(int life, int fadeTime) {
        this.life = life;
        this.fadeTime = fadeTime;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted > life) {
            int dt = ticksExisted - life;
            float alpha = 1 - (float) dt / fadeTime;
            if (alpha < 0) {
                setDead();
                alpha = 0;
            }
            color.setAlpha(Colors.f2i(alpha * startAlpha));
        } else if (ticksExisted < fadeInTime) {
            color.setAlpha(Colors.f2i(startAlpha * ((float) ticksExisted / fadeInTime)));
        } else {
            color.setAlpha(Colors.f2i(startAlpha));
        }

        motionY -= gravity;
    }

    public void fromTemplate(Particle template) {
        this.texture = template.texture;
        this.color = new Color(template.color);
        this.size = template.size;
        this.hasLight = template.hasLight;
        this.fadeTime = template.fadeTime;
        this.life = template.life;
        this.gravity = template.gravity;
        this.needRigidbody = template.needRigidbody;
        this.fadeInTime = template.fadeInTime;
        this.customRotation = template.customRotation;
        this.updated = false;
    }

    @Override
    protected void onFirstUpdate() {
        updated = true;
        if (needRigidbody)
            this.addMotionHandler(new Rigidbody());
        creationTime = GameTimer.getTime();
        startAlpha = Colors.i2f(color.getAlpha());
    }

    public double getParticleLife() {
        return GameTimer.getTime() - creationTime;
    }

    public double getMaxLife() {
        return life;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
    }

    @Override
    public void updateSprite(Sprite s) {
        s.cullFace = false;
        s.height = s.width = size;
        s.texture = texture;
        s.color = color;
        s.hasLight = hasLight;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public boolean needViewOptimize() {
        return false;
    }

}
