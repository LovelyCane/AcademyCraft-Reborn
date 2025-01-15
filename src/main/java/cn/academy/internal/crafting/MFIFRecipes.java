package cn.academy.internal.crafting;

import cn.academy.AcademyCraftItemList;
import cn.academy.internal.tileentity.TileMetalFormer.Mode;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

// TODO: Make recipes data driven
public class MFIFRecipes {

    @StateEventCallback
    static void init(FMLInitializationEvent ev) {
        ImagFusorRecipes ifr = ImagFusorRecipes.INSTANCE;
        ifr.addRecipe(new ItemStack(AcademyCraftItemList.CRYSTAL_LOW), 3000, new ItemStack(AcademyCraftItemList.CRYSTAL_NORMAL));
        ifr.addRecipe(new ItemStack(AcademyCraftItemList.CRYSTAL_NORMAL), 8000, new ItemStack(AcademyCraftItemList.CRYSTAL_PURE));

        MetalFormerRecipes mfr = MetalFormerRecipes.INSTANCE;
        mfr.add(new ItemStack(AcademyCraftItemList.IMAG_SILICON_INGOT), new ItemStack(AcademyCraftItemList.WAFER, 2), Mode.INCISE);
        mfr.add(new ItemStack(AcademyCraftItemList.WAFER), new ItemStack(AcademyCraftItemList.IMAG_SILICON_PIECE, 4), Mode.INCISE);
        mfr.add(new ItemStack(AcademyCraftItemList.DATA_CHIP), new ItemStack(AcademyCraftItemList.CALC_CHIP), Mode.ETCH);
        mfr.add(new ItemStack(Items.IRON_INGOT), new ItemStack(AcademyCraftItemList.REINFORCED_IRON_PLATE), Mode.PLATE);
        mfr.add(new ItemStack(AcademyCraftItemList.CONSTRAINT_INGOT), new ItemStack(AcademyCraftItemList.CONSTRAINT_PLATE), Mode.PLATE);
        mfr.add(new ItemStack(AcademyCraftItemList.ITEM_IMAGSIL_ORE), new ItemStack(AcademyCraftItemList.IMAG_SILICON_INGOT, 4), Mode.REFINE);
        mfr.add(new ItemStack(AcademyCraftItemList.ITEM_CONSTRAINT_METAL), new ItemStack(AcademyCraftItemList.CONSTRAINT_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(AcademyCraftItemList.ITEM_RESO_ORE), new ItemStack(AcademyCraftItemList.RESO_CRYSTAL, 3), Mode.REFINE);
        mfr.add(new ItemStack(AcademyCraftItemList.ITEM_CRYSTAL_ORE), new ItemStack(AcademyCraftItemList.CRYSTAL_LOW, 4), Mode.REFINE);

        addOreDictRefineRecipe("oreGold",new ItemStack(Items.GOLD_INGOT,2));
        addOreDictRefineRecipe("oreIron",new ItemStack(Items.IRON_INGOT,2));
        addOreDictRefineRecipe("oreEmerald",new ItemStack(Items.EMERALD,2));
        addOreDictRefineRecipe("oreQuartz",new ItemStack(Items.QUARTZ,2));
        addOreDictRefineRecipe("oreDiamond",new ItemStack(Items.DIAMOND,2));
        addOreDictRefineRecipe("oreRedstone",new ItemStack(Blocks.REDSTONE_BLOCK));
        addOreDictRefineRecipe("oreLapis",new ItemStack(Items.DYE,12,4));
        addOreDictRefineRecipe("oreCoal",new ItemStack(Items.COAL,2));
        addDefaultOreDictRefineRecipe("Copper");
        addDefaultOreDictRefineRecipe("Tin");
        addDefaultOreDictRefineRecipe("Silver");
        addDefaultOreDictRefineRecipe("Lead");
        addDefaultOreDictRefineRecipe("Aluminum");
        addDefaultOreDictRefineRecipe("Nickel");
        addDefaultOreDictRefineRecipe("Platinum");
        addDefaultOreDictRefineRecipe("Iridium");
        addDefaultOreDictRefineRecipe("Mithril");

    }

    private static void addOreDictRefineRecipe(String orename,ItemStack output)
    {
        for (ItemStack ore : OreDictionary.getOres(orename))
            MetalFormerRecipes.INSTANCE.add(ore,output,Mode.REFINE);
    }

    private static void addOreDictRefineRecipe(String orename, String outputname)
    {
        List<ItemStack> outputList = OreDictionary.getOres(outputname);
        if (outputList == null || outputList.size() == 0)
            return;
        ItemStack output = outputList.get(0).copy();
        List<ItemStack> oreList = OreDictionary.getOres(orename);
        if (oreList == null || oreList.size() == 0)
            return;
        ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(oreList.get(0));
        if (stack.isEmpty())
            return;
        int outputsize = stack.getCount();
        outputsize = outputsize < 32 ? (2 * outputsize) : 64;
        output.setCount(outputsize);
        for(ItemStack ore : oreList)
        {
            MetalFormerRecipes.INSTANCE.add(ore, output, Mode.REFINE);
        }
    }

    private static void addDefaultOreDictRefineRecipe(String orename)
    {
        addOreDictRefineRecipe("ore"+orename,"ingot"+orename);
    }
}
