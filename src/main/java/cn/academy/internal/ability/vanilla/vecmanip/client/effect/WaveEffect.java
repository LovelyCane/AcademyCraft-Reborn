package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.internal.entity.LocalEntity;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class WaveEffect extends LocalEntity {
    public static class Ring {
        public int life;
        public double offset;
        public double size;
        public int timeOffset;

        public Ring(int life, double offset, double size, int timeOffset) {
            this.life = life;
            this.offset = offset;
            this.size = size;
            this.timeOffset = timeOffset;
        }
    }

    public List<Ring> ringList = new ArrayList<>();
    public final int life = 15;

    public WaveEffect(World world, int rings, double size) {
        super(world);

        for (int idx = 0; idx < rings; idx++) {
            ringList.add(new Ring(
                    RandUtils.rangei(8, 12),
                    idx * 1.5 + RandUtils.ranged(-0.3, 0.3),
                    size * RandUtils.ranged(0.8, 1.2),
                    idx * 2 + RandUtils.rangei(-1, 1)
            ));
        }

        ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        if (ticksExisted >= life) {
            setDead();
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}