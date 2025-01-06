package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.ClientRuntime.IActivateHandler;
import cn.academy.ability.context.RegClientContext;
import cn.academy.ability.vanilla.vecmanip.client.effect.StormWingEffect;
import cn.academy.internel.sound.ACSounds;
import cn.academy.internel.sound.FollowEntitySound;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.ability.vanilla.vecmanip.skill.StormWingContext.KEY_GROUP;

@SideOnly(Side.CLIENT)
@RegClientContext(StormWingContext.class)
@SuppressWarnings("unused")
public class StormWingContextC extends ClientContext {
    private IActivateHandler activateHandler;
    private FollowEntitySound loopSound;
    StormWingContext par;
    public StormWingContextC(StormWingContext par) {
        super(par);
        this.par = par;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void l_makeAlive() {
        if (isLocal()) {
            activateHandler = new IActivateHandler() {
                @Override
                public boolean handles(EntityPlayer player) {
                    return true;
                }

                @Override
                public String getHint() {
                    return IActivateHandler.ENDSPECIAL;
                }

                @Override
                public void onKeyDown(EntityPlayer player) {
                    terminate();
                }
            };
            clientRuntime().addActivateHandler(activateHandler);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void l_terminate() {
        if (isLocal()) {
            clientRuntime().clearKeys(KEY_GROUP);
            clientRuntime().removeActiveHandler(activateHandler);
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void c_makealive() {
        world().spawnEntity(new StormWingEffect(par));

        loopSound = new FollowEntitySound(player, "vecmanip.storm_wing", SoundCategory.AMBIENT).setLoop();
        ACSounds.playClient(loopSound);
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_terminate() {
        loopSound.stop();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_tick() {
        for (int i = 0; i < 12; i++) {
            double theta = ranged(0, Math.PI * 2);
            double phi = ranged(-Math.PI, Math.PI);
            double r = ranged(3, 8);

            double rzx = r * Math.sin(phi);
            double cth = Math.cos(theta);
            double sth = Math.sin(theta);
            double dx = rzx * cth;
            double dy = r * Math.cos(phi);
            double dz = rzx * sth;

            MyDustParticle particle = new MyDustParticle(
                    world(),
                    player.posX + dx, player.posY + dy, player.posZ + dz,
                    sth * 0.7f, ranged(-0.01f, 0.05f), -cth * 0.7f,
                    Blocks.DIRT.getDefaultState()
            );
            particle.setBlockPos(player.getPosition());
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    public static class MyDustParticle extends ParticleBlockDust {
        public MyDustParticle(World world, double x, double y, double z,
                              double vx, double vy, double vz, IBlockState state) {
            super(world, x, y, z, vx, vy, vz, state);
            this.particleGravity = 0.02f;
            this.multipleParticleScaleBy(0.5f);
        }
    }

    private double ranged(double min, double max) {
        return min + (max - min) * Math.random();
    }
}
