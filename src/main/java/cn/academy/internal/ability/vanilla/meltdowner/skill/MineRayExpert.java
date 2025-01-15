package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityMineRayExpert;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MineRayExpert extends MineRaysBase {
    public static final MineRayExpert INSTANCE = new MineRayExpert();

    public MineRayExpert() {
        super("expert", 4);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, ExpertMRContext::new);
    }
}

class ExpertMRContext extends MRContext {
    public ExpertMRContext(EntityPlayer p) {
        super(p, MineRayExpert.INSTANCE);
        setRange(20);
        setHarvestLevel(5);
        setSpeed(0.5f, 1f);
        setConsumption(25f, 15f);
        setOverload(300f, 200f);
        setCooldown(60f, 30f);
        setExpIncr(0.0003f);
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, Block block) {
        IBlockState state = world.getBlockState(pos);
        SoundEvent breakSound = block.getSoundType(state, world, pos, player).getBreakSound();

        world.playSound(player, pos, breakSound, SoundCategory.BLOCKS, 0.5f, 1f);

        block.dropBlockAsItemWithChance(world, pos, state, 1.0f, 0);

        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}

@SideOnly(Side.CLIENT)
@RegClientContext(ExpertMRContext.class)
class ExpertMRContextC extends MRContextC {
    public ExpertMRContextC(ExpertMRContext par) {
        super(par);
    }

    @Override
    public Entity createRay() {
        return new EntityMineRayExpert(player);
    }
}