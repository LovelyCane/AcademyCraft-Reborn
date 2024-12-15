package cn.lambdalib2.crafting;

import cn.lambdalib2.util.Debug;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author WeAthFolD
 */
public class SmeltingRegistry implements IRecipeRegistry {

    public static final SmeltingRegistry INSTANCE = new SmeltingRegistry();

    private SmeltingRegistry() { }

    @Override
    public void register(String type, ItemStack output, Object[] input, int width, int height, float experience) {
        if (width != 1 || height != 1) {
            throw new IllegalArgumentException("You can only specify 1 input for smelting!");
        }

        if (input[0] instanceof String) {
            for (ItemStack stack : OreDictionary.getOres((String) input[0])) {
                register(type, output, new ItemStack[] { stack }, width, height, experience);
            }
        } else {
            Object in = input[0];

            if (in instanceof ItemStack)
                GameRegistry.addSmelting((ItemStack) in, output, experience);
            else if (in instanceof Block)
                GameRegistry.addSmelting((Block) in, output, experience);
            else if (in instanceof Item)
                GameRegistry.addSmelting((Item) in, output, experience);

            Debug.debug("[Smelting] " +
                    in + " => " + RecipeRegistry.reprStack(output));
        }
    }

}
