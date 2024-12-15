package cn.lambdalib2.crafting;

import cn.lambdalib2.util.Debug;
import com.google.common.base.Joiner;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.UUID;

/**
 * @author EAirPeter
 */
public class ShapelessOreRegistry implements IRecipeRegistry {

    private final RecipeRegistry registry;

    public ShapelessOreRegistry(RecipeRegistry recipeRegistry) {
        registry = recipeRegistry;
    }

    @Override
    public void register(String type, ItemStack output, Object[] input, int width, int height, float experience) {
        Ingredient[] ing = new Ingredient[input.length];
        for (int i = 0; i < input.length; i++) {
            if (input[i] instanceof ItemStack) {
                ing[i] = Ingredient.fromStacks((ItemStack) input[i]);
            } else if (input[i] instanceof Block) {
                ing[i] = Ingredient.fromItem(Item.getItemFromBlock((Block) input[i]));
            } else if (input[i] instanceof Item) {
                ing[i] = Ingredient.fromItem((Item) input[i]);
            } else if (input[i] instanceof String) {
                NonNullList<ItemStack> list = OreDictionary.getOres((String) input[i]);
                ItemStack[] iss = new ItemStack[list.size()];
                list.toArray(iss);
                ing[i] = Ingredient.fromStacks(iss);
            }
        }
        GameRegistry.addShapelessRecipe(new ResourceLocation(registry.PREFIX + ':' + UUID.randomUUID()),
                new ResourceLocation(registry.PREFIX), output, ing);

        Debug.debug("[ShapelessOre] " +
                RecipeRegistry.reprStack(output) + " => " +
                Joiner.on(',').join(input));
    }

}
