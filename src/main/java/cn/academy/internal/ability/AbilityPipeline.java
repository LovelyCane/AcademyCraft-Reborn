package cn.academy.internal.ability;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftConfig;
import cn.academy.internal.event.BlockDestroyEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

/**
 * INTERNAL CLASS
 */
public class AbilityPipeline {
    private AbilityPipeline() {
    }

    /**
     * @return Whether we can break any block at all
     */
    static boolean canBreakBlock(World world) {
        return propDestroyBlocks || ArrayUtils.contains(propWorldsDestroyingBlocks, String.valueOf(world.provider.getDimension())) || ArrayUtils.contains(propWorldsDestroyingBlocks, world.provider.getSaveFolder());
    }

    static boolean isAllWorldDisableBreakBlock() {
        return !propDestroyBlocks && propWorldsDestroyingBlocks.length == 0;
    }

    /**
     * @return Whether PvP is enabled.
     */
    static boolean canAttackPlayer() {
        return propAttackPlayer;
    }

    /**
     * Tests if the block at the specified coordinates can be broken by a
     * certain player.
     *
     * @return Whether the block can be really broken.
     */
    static boolean canBreakBlock(World world, EntityPlayer player, int x, int y, int z) {
        return !MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, player, new BlockPos(x, y, z)));
    }

    static boolean canBreakBlock(World world, EntityPlayer player, BlockPos pos) {
        return !MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, player, pos));
    }

    public static boolean canUseMouseWheel() {
        return propUseMouseWheel;
    }

    // PROPERTIES
    private static final AcademyCraftConfig.Generic generic = AcademyCraft.academyCraftConfig.getGeneric();
    private static final boolean propAttackPlayer = generic.isAttackPlayer();
    private static final boolean propDestroyBlocks = generic.isDestroyBlocks();
    private static final String[] propWorldsDestroyingBlocks = generic.getWorldsWhitelistedDestroyingBlocks();
    private static final boolean propUseMouseWheel = generic.isUseMouseWheel();


    @SubscribeEvent
    public static void onBlockDestroy(BlockDestroyEvent event) {
        if (!canBreakBlock(event.world))
            event.setCanceled(true);
    }
}