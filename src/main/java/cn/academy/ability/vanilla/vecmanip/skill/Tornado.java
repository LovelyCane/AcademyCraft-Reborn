package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.vanilla.vecmanip.client.effect.TornadoEffect;
import cn.academy.entity.LocalEntity;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Tornado extends LocalEntity {
    public final TornadoEffect theTornado;
    private final PlasmaCannonContext ctx;

    private boolean dead = false;
    private int deadTick = 0;

    public Tornado(PlasmaCannonContext ctx) {
        super(ctx.player.world);
        this.ctx = ctx;
        EntityPlayer player = ctx.player;
        this.theTornado = new TornadoEffect(12, 8, 1, 0.3);

        Vec3d initPos;
        Vec3d p0 = ctx.chargePosition;
        Vec3d p1 = new Vec3d(p0.x, p0.y - 20.0, p0.z);
        RayTraceResult result = Raytrace.perform(player.world, p0, p1, EntitySelectors.nothing());

        if (result.typeOfHit != RayTraceResult.Type.MISS) {
            initPos = result.hitVec;
        } else {
            initPos = p1;
        }
        this.setPosition(initPos.x, initPos.y, initPos.z);

        this.ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        if (ctx.state == PlasmaCannonContext.STATE_GO || ctx.getStatus() == PlasmaCannonContext.Status.TERMINATED) {
            dead = true;
        }

        if (dead) {
            deadTick++;
            if (deadTick == 30) {
                setDead();
            }
        }

        theTornado.alpha = (getAlpha() * 0.5f);
    }

    private float getAlpha() {
        if (!dead) {
            return ticksExisted < 20.0f ? ticksExisted / 20.0f : 1.0f;
        } else {
            return 1 - deadTick / 20.0f;
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
