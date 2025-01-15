package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.internal.ability.context.Context.Status;
import cn.academy.internal.ability.vanilla.vecmanip.skill.StormWingContext;
import cn.academy.internal.entity.LocalEntity;
import cn.lambdalib2.vis.CompTransform;
import net.minecraft.client.entity.AbstractClientPlayer;

import java.util.ArrayList;
import java.util.List;

public class StormWingEffect extends LocalEntity {
    final int STATE_CHARGE = 0;
    public static class SubEffect {
        public TornadoEffect eff;
        public CompTransform trans;

        public SubEffect(TornadoEffect eff, CompTransform trans) {
            this.eff = eff;
            this.trans = trans;
        }
    }

    public final List<SubEffect> tornadoList = new ArrayList<>();
    private final AbstractClientPlayer player;

    private static final int TERMINATE_TICK = 15;
    private boolean terminated = false;
    private int terminateTick = 0;
    StormWingContext ctx;

    public StormWingEffect(StormWingContext ctx) {
        super(ctx.player.world);
        this.ctx = ctx;
        this.player = (AbstractClientPlayer) ctx.player;

        for (int i = 0; i < 4; i++) {
            tornadoList.add(new SubEffect(new TornadoEffect(2, 0.16, 1.0,2.0), new CompTransform()));
        }

        int sep = 45;
        tornadoList.get(0).trans.setTransform(-0.1, -0.3, 0.1).setRotation(0, sep, sep);
        tornadoList.get(1).trans.setTransform(0.1, -0.3, 0.1).setRotation(0, -sep, -sep);
        tornadoList.get(2).trans.setTransform(-0.1, -0.5, -0.1).setRotation(0, -sep, sep);
        tornadoList.get(3).trans.setTransform(0.1, -0.5, -0.1).setRotation(0, sep, -sep);

        setRotation(player.renderYawOffset, player.rotationPitch);
        updatePosition();
        ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        if (ctx.getStatus() == Status.TERMINATED) {
            terminated = true;
        }

        if (terminated) {
            terminateTick++;
            if (terminateTick > TERMINATE_TICK) {
                setDead();
            }
        }

        updatePosition();
        setRotation(player.renderYawOffset, player.rotationPitch);

        double alpha;
        if (ctx.getState() == STATE_CHARGE) {
            alpha = ctx.getStateTick() / ctx.chargeTime * 0.7;
        } else if (!terminated) {
            alpha = 0.7;
        } else {
            alpha = 0.7 * (1 - terminateTick / (double) TERMINATE_TICK);
        }

        for (SubEffect subEffect : tornadoList) {
            subEffect.eff.alpha = alpha;
        }
    }

    private void updatePosition() {
        setPosition(player.posX, player.posY + 1.6, player.posZ);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}