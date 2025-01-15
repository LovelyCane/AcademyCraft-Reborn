package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.internal.ability.context.Context.Status;
import cn.academy.internal.ability.vanilla.vecmanip.skill.PlasmaCannonContext;
import cn.academy.internal.entity.LocalEntity;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.MathUtils;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlasmaBodyEffect extends LocalEntity {
    public PlasmaCannonContext ctx;

    // Ball instance definition
    public static class BallInst {
        public float size;
        public Vector3f center;
        public TrigPar hmove;
        public TrigPar vmove;

        public BallInst(float size, Vector3f center, TrigPar hmove, TrigPar vmove) {
            this.size = size;
            this.center = center;
            this.hmove = hmove;
            this.vmove = vmove;
        }
    }

    // Trigonometric parameters for movement
    public static class TrigPar {
        public float amp;
        public float speed;
        public float dphase;

        public TrigPar(float amp, float speed, float dphase) {
            this.amp = amp;
            this.speed = speed;
            this.dphase = dphase;
        }

        public float phase(float time) {
            return speed * time - dphase;
        }
    }

    public List<BallInst> balls = new ArrayList<>();
    public float alpha = 0.0f;
    private double initTime;

    public PlasmaBodyEffect(World world, PlasmaCannonContext ctx) {
        super(world);
        this.ctx = ctx;
        Random rand = new Random();

        // Initialize balls with random parameters
        for (int i = 0; i < 4; i++) {
            balls.add(new BallInst(
                    rand.nextFloat() * (1.5f - 1) + 1,
                    new Vector3f(rand.nextFloat() * 3 - 1.5f, rand.nextFloat() * 3 - 1.5f, rand.nextFloat() * 3 - 1.5f),
                    nextTrigPar(),
                    nextTrigPar()
            ));
        }

        for (int i = 0; i < rand.nextInt(2) + 4; i++) {
            balls.add(new BallInst(
                    rand.nextFloat() * (0.3f - 0.1f) + 0.1f,
                    new Vector3f(rand.nextFloat() * 6 - 3f, rand.nextFloat() * 6 - 3f, rand.nextFloat() * 6 - 3f),
                    nextTrigPar(2.5f),
                    nextTrigPar(2.5f)
            ));
        }

        setSize(10, 10);
        ignoreFrustumCheck = true;
        this.initTime = GameTimer.getTime();
    }

    // Helper method to generate random trig parameters
    private TrigPar nextTrigPar(float size) {
        Random rand = new Random();
        float amp = rand.nextFloat() * (2f - 1.4f) + 1.4f;
        float speed = rand.nextFloat() * (0.7f - 0.5f) + 0.5f;
        float dphase = rand.nextFloat() * MathUtils.PI_F * 2;
        return new TrigPar(amp * size, speed, dphase);
    }

    private TrigPar nextTrigPar() {
        return nextTrigPar(1.0f);
    }

    // Update logic for alpha value
    public void updateAlpha() {
        float dt = (float) (GameTimer.getTime() - initTime);
        boolean terminated = ctx.getStatus() == Status.TERMINATED;
        float desiredAlpha = terminated ? 0 : 1;
        alpha = moveTowards(alpha, desiredAlpha, dt * (terminated ? 1f : 0.3f));
        initTime = GameTimer.getTime();
    }

    // Utility method for smoothing movement
    private float moveTowards(float from, float to, float max) {
        float delta = to - from;
        return from + Math.min(Math.abs(delta), max) * Math.signum(delta);
    }

    // On update callback
    @Override
    public void onUpdate() {
        if (ctx.getStatus() == Status.TERMINATED && Math.abs(alpha) <= 1e-3f) {
            setDead();
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    // Get delta time
    public double deltaTime() {
        return GameTimer.getTime() - initTime;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }

    @Override
    public void setDead() {
        super.setDead();
    }
}
