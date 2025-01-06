package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.api.AbilityAPIExt;
import cn.academy.ability.context.Context;
import cn.academy.ability.context.DelegateState;
import cn.academy.ability.context.IConsumptionProvider;
import cn.academy.ability.context.IStateProvider;
import cn.academy.util.Plotter;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

import static cn.lambdalib2.util.MathUtils.lerpf;
import static cn.lambdalib2.util.VecUtils.multiply;

@SuppressWarnings("unused")
public class GroundshockContext extends Context<Groundshock> implements IConsumptionProvider, IStateProvider {
    private static final String MSG_PERFORM = "perform";
    private int localTick = 0;
    private static final double GROUND_BREAK_PROB = 0.3;

    public GroundshockContext(EntityPlayer p) {
        super(p, Groundshock.INSTANCE);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    public void l_tick() {
        localTick += 1;

        float pitchDelta = 0f;
        if (localTick < 4) {
            pitchDelta = localTick / 4.0f;
        } else if (localTick <= 20) {
            pitchDelta = 1.0f;
        } else if (localTick <= 25) {
            pitchDelta = 1.0f - (localTick - 20) / 5.0f;
        }

        player.rotationPitch -= pitchDelta * 0.2f;
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    public void l_keyUp() {
        if (localTick >= 5) {
            sendToServer(MSG_PERFORM);
        } else {
            terminate();
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    public void l_keyAbort() {
        terminate();
    }

    @Listener(channel = MSG_PERFORM, side = Side.SERVER)
    public void s_perform() {
        if (player.onGround && consume()) {
            Vec3d planeLook = player.getLookVec().normalize();
            Plotter plotter = new Plotter((int) Math.floor(player.posX), (int) Math.floor(player.posY) - 1, (int) Math.floor(player.posZ), planeLook.x, 0, planeLook.z);

            int iter = maxIter;

            // wow
            Set<BlockPos> dejavuBlocks = new HashSet<>();
            Set<Entity> dejavuEnt = new HashSet<>();

            Vec3d rot = planeLook.rotateYaw(90);
            List<Delta> deltas = Arrays.asList(new Delta(new Vec3d(0.0, 0.0, 0.0), 1.0), new Delta(rot, 0.7), new Delta(multiply(rot, -1), 0.7), new Delta(multiply(rot, 2), 0.3), new Delta(multiply(rot, -2), 0.3));

            while (energy > 0 && iter > 0) {
                int[] next = plotter.next();
                int x = next[0], y = next[1], z = next[2];

                iter -= 1;

                for (Delta delta : deltas) {
                    BlockPos pos = new BlockPos((x + delta.getDelta().x), (y + delta.getDelta().y), (z + delta.getDelta().z));
                    Block block = world().getBlockState(pos).getBlock();

                    if (RandUtils.nextDouble() < delta.getProb()) {
                        if (block != Blocks.AIR && !dejavuBlocks.contains(pos)) {
                            dejavuBlocks.add(pos);

                            if (block.equals(Blocks.STONE)) {
                                if (Blocks.COBBLESTONE != null) {
                                    world().setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
                                }
                                energy -= 0.4;
                            } else if (block.equals(Blocks.GRASS)) {
                                world().setBlockState(pos, Objects.requireNonNull(Blocks.DIRT).getDefaultState());
                                energy -= 0.2;
                            } else if (block.equals(Blocks.FARMLAND)) {
                                energy -= 0.1;
                            } else {
                                energy -= 0.5;
                            }

                            if (RandUtils.nextDouble() < GROUND_BREAK_PROB) {
                                breakWithForce(x, y, z, false);
                            }

                            AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - 0.2, pos.getY() - 0.2, pos.getZ() - 0.2, pos.getX() + 1.4, pos.getY() + 2.2, pos.getZ() + 1.4);
                            List<Entity> entities = WorldUtils.getEntities(world(), aabb, EntitySelectors.living().and(EntitySelectors.exclude(player)));
                            for (Entity entity : entities) {
                                if (!dejavuEnt.contains(entity)) {
                                    dejavuEnt.add(entity);
                                    energy -= 1;
                                    ctx.attack(entity, damage);
                                    entity.motionY = ySpeed;
                                    ctx.addSkillExp(0.002f);
                                }
                            }
                        }
                    }

                    for (int d = 1; d <= 3; d++) {
                        breakWithForce(x, y + d, z, false);
                    }
                }
            }

            energy = Double.MAX_VALUE;
            if (ctx.getSkillExp() == 1) {
                int x0 = (int) player.posX, y0 = (int) player.posY, z0 = (int) player.posZ;
                for (int x = x0 - 5; x < x0 + 5; x++) {
                    for (int y = y0 - 1; y < y0 + 1; y++) {
                        for (int z = z0 - 5; z < z0 + 5; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            if (world().getBlockState(pos).getBlockHardness(world(), pos) <= 0.6) {
                                breakWithForce(x, y, z, true);
                            }
                        }
                    }
                }
            }

            ctx.addSkillExp(0.001f);
            ctx.setCooldown(cooldown);
            sendToClient(MSG_PERFORM, dejavuBlocks.stream().map(v -> new int[]{v.getX(), v.getY(), v.getZ()}).toArray());
        }
        terminate();
    }

    @Override
    public float getConsumptionHint() {
        return consumption;
    }

    @Override
    public DelegateState getState() {
        return localTick < 5 ? DelegateState.CHARGE : DelegateState.ACTIVE;
    }

    private final double initEnergy = lerpf(60, 120, ctx.getSkillExp());
    double energy = initEnergy;
    private final float damage = lerpf(4, 6, ctx.getSkillExp());
    private final float consumption = lerpf(80, 150, ctx.getSkillExp());
    private final float overload = lerpf(15, 10, ctx.getSkillExp());
    private final int maxIter = (int) lerpf(10, 25, ctx.getSkillExp());
    private final int cooldown = (int) lerpf(80, 40, ctx.getSkillExp());
    private final float dropRate = lerpf(0.3f, 1.0f, ctx.getSkillExp());
    private final float ySpeed = RandUtils.rangef(0.6f, 0.9f) * lerpf(0.8f, 1.3f, ctx.getSkillExp());

    public boolean consume() {
        return ctx.consume(overload, consumption);
    }

    private void breakWithForce(int x, int y, int z, boolean drop) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (ctx.canBreakBlock(world(), x, y, z)) {
            Block block = world().getBlockState(blockPos).getBlock();
            IBlockState state = world().getBlockState(blockPos);
            float hardness = world().getBlockState(blockPos).getBlockHardness(world(), blockPos);
            if (hardness >= 0 && energy >= hardness && block != Blocks.FARMLAND && !state.getMaterial().isLiquid()) {
                energy -= hardness;
                if (drop && RandUtils.nextFloat() < dropRate) {
                    block.dropBlockAsItemWithChance(world(), blockPos, world().getBlockState(blockPos), 1.0f, 0);
                }
                world().setBlockToAir(blockPos);
                world().playSound(x + 0.5, y + 0.5, z + 0.5, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.AMBIENT, 0.5f, 1f, false);
            }
        }
    }

    private static class Delta {
        private final Vec3d delta;
        private final double prob;

        public Delta(Vec3d delta, double prob) {
            this.delta = delta;
            this.prob = prob;
        }

        public Vec3d getDelta() {
            return delta;
        }

        public double getProb() {
            return prob;
        }
    }
}
