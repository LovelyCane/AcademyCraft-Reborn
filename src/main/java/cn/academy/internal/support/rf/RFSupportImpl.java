package cn.academy.internal.support.rf;

import cn.academy.AcademyCraftBlockList;
import cn.academy.AcademyCraftItemList;
import cn.academy.internal.support.EnergyBlockHelper;
import cn.lambdalib2.util.SideUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static cn.academy.AcademyCraftTileEntityList.TILE_ENTITY_LIST;

public class RFSupportImpl {
    /**
     * The convert rate (1IF = <CONV_RATE> RF)
     */
    public static final double CONV_RATE = 4;

    public static final Block rfInput = new BlockRFInput();
    public static final Block rfOutput = new BlockRFOutput();

    public static final ItemBlock item_rfInput = new ItemBlock(rfInput);
    public static final ItemBlock item_rfOutput = new ItemBlock(rfOutput);


    // Convert macros, dividing by hand is error-prone

    /**
     * Converts RF to equivalent amount of IF.
     */
    public static double rf2if(int rfEnergy) {
        return rfEnergy / CONV_RATE;
    }

    /**
     * Converts IF to equivalent amount of RF.
     */
    public static int if2rf(double ifEnergy) {
        return (int) (ifEnergy * CONV_RATE);
    }

    @SubscribeEvent
    @Optional.Method(modid = "redstoneflux")
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        EnergyBlockHelper.register(new RFProviderManager());
        EnergyBlockHelper.register(new RFReceiverManager());

        GameRegistry.addShapedRecipe(new ResourceLocation("academy", "rf_input"), null, new ItemStack(rfInput), "abc", " d ", 'a', AcademyCraftItemList.ENERGY_UNIT, 'b', AcademyCraftBlockList.MACHINE_FRAME, 'c', AcademyCraftItemList.CONSTRAINT_PLATE, 'd', AcademyCraftItemList.ENERGY_CONVERT_COMPONENT);

        GameRegistry.addShapedRecipe(new ResourceLocation("academy", "rf_output"), null, new ItemStack(rfOutput), "abc", " d ", 'a', AcademyCraftItemList.ENERGY_UNIT, 'b', AcademyCraftBlockList.MACHINE_FRAME, 'c', AcademyCraftItemList.RESO_CRYSTAL, 'd', AcademyCraftItemList.ENERGY_CONVERT_COMPONENT);

        GameRegistry.addShapedRecipe(new ResourceLocation("academy", "rf_input_output"), null, new ItemStack(rfInput), "X", 'X', new ItemStack(rfOutput));
        GameRegistry.addShapedRecipe(new ResourceLocation("academy", "rf_output_input"), null, new ItemStack(rfOutput), "X", 'X', new ItemStack(rfInput));
    }

    @SubscribeEvent
    @Optional.Method(modid = "redstoneflux")
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        rfInput.setRegistryName("academy:ac_rf_input");
        rfInput.setTranslationKey("ac_rf_input");
        event.getRegistry().register(rfInput);

        rfOutput.setRegistryName("academy:ac_rf_output");
        rfOutput.setTranslationKey("ac_rf_output");
        event.getRegistry().register(rfOutput);

        TILE_ENTITY_LIST.add(TileRFInput.class);
        TILE_ENTITY_LIST.add(TileRFOutput.class);
    }

    @SubscribeEvent
    @Optional.Method(modid = "redstoneflux")
    public static void registerItems(RegistryEvent.Register<Item> event) {
        item_rfInput.setRegistryName(rfInput.getRegistryName());
        item_rfInput.setTranslationKey(rfInput.getTranslationKey());
        event.getRegistry().register(item_rfInput);
        if (SideUtils.isClient()) {
            ModelLoader.setCustomModelResourceLocation(item_rfInput, 0, new ModelResourceLocation("academy:eu_input", "inventory"));
        }

        item_rfOutput.setRegistryName(rfOutput.getRegistryName());
        item_rfOutput.setTranslationKey(rfOutput.getTranslationKey());
        event.getRegistry().register(item_rfOutput);
        if (SideUtils.isClient()) {
            ModelLoader.setCustomModelResourceLocation(item_rfOutput, 0, new ModelResourceLocation("academy:eu_input", "inventory"));
        }
    }
}
