package cn.lambdalib2.crafting;

import net.minecraft.item.ItemStack;

/**
 * @author EAirPeter
 */
public interface IRecipeRegistry {

    /**
     * Register the specified recipe.
     * 
     * @param type
     * @param output
     *            Output slot
     * @param input
     *            Input objects in row-major order. table[r][c] equals input[r *
     *            height + c]. Every element is either an String(oreDict) or
     *            ItemStack.
     * @param width
     *            input grid width
     * @param height
     *            input grid height
     * @param experience
     *            The experience get when uses the recipe. 0 if not specified.
     */
    void register(String type, ItemStack output, Object[] input, int width, int height, float experience);

}
