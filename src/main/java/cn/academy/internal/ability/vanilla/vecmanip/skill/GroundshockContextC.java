package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.ability.vanilla.generic.client.effect.SmokeEffect;
import cn.academy.internal.ability.vanilla.util.HandlerLifePeroidEvent;
import cn.academy.internal.sound.ACSounds;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import static cn.lambdalib2.util.RandUtils.*;

@SideOnly(Side.CLIENT)
@RegClientContext(GroundshockContext.class)
@SuppressWarnings("unused")
public class GroundshockContextC extends ClientContext {
    final String MSG_PERFORM = "perform";
    GroundshockContext par;

    public GroundshockContextC(GroundshockContext par) {
        super(par);
        this.par = par;
    }

    @Listener(channel = MSG_PERFORM, side = {Side.CLIENT})
    private void c_perform(ArrayList<BlockPos> dejavuBlocks) {
        if (isLocal()) {
            par.consume();

            // Starts a coroutine that makes player's look direction slash down.
            MinecraftForge.EVENT_BUS.register(new HandlerLifePeroidEvent(4) {
                @Override
                public boolean onTick() {
                    player.rotationPitch += 3.4f;
                    return true;
                }

                @Override
                public void onDeath() {
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            });
        }

        ACSounds.playClient(player, "vecmanip.groundshock", SoundCategory.AMBIENT, 2);

        for (BlockPos blockPos : dejavuBlocks) {
            BlockPos pt = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            for (int i = 0; i < rangei(4, 8); i++) {
                // Generates random velocities
                double randvel = ranged(-0.2, 0.2);

                IBlockState is = world().getBlockState(pt);

                ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
                ParticleDigging particle = (ParticleDigging) particleManager.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), pt.getX() + nextDouble(), pt.getY() + 1 + nextDouble() * 0.5 + 0.2, pt.getZ() + nextDouble(), randvel, 0.1 + nextDouble() * 0.2, randvel, Block.getIdFromBlock(is.getBlock()), EnumFacing.UP.ordinal());
                if (particle != null) {
                    particle.setBlockPos(pt);
                }
            }

            if (nextFloat() < 0.5f) {
                SmokeEffect eff = new SmokeEffect(world());
                double[] pos = {pt.getX() + 0.5 + ranged(-0.3, 0.3), pt.getY() + 1 + ranged(0, 0.2), pt.getZ() + 0.5 + ranged(-0.3, 0.3)};
                double[] vel = {ranged(-0.03, 0.03), ranged(0.03, 0.06), ranged(-0.03, 0.03)};

                eff.forceSpawn = true;
                eff.setPosition(pos[0], pos[1], pos[2]);
                eff.motionX = vel[0];
                eff.motionY = vel[1];
                eff.motionZ = vel[2];
                world().spawnEntity(eff);
            }
        }
    }
}

