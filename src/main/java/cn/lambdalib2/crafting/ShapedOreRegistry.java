package cn.lambdalib2.crafting;

import cn.lambdalib2.util.Debug;
import com.google.common.base.Joiner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.UUID;

/**
 * @author EAirPeter
 */
public class ShapedOreRegistry implements IRecipeRegistry {

    private final RecipeRegistry registry;

    public ShapedOreRegistry(RecipeRegistry recipeRegistry) {
        registry = recipeRegistry;
    }

    @Override
    public void register(String type, ItemStack output, Object[] input, int width, int height, float experience) {
        boolean mirrored = !type.equals("shaped_s");
        int pairs = 0;
        for (Object elem : input)
            if (elem != null)
                ++pairs;
        Object[] recipe = new Object[height + pairs * 2];
        int index = 0;
        int _i = height;
        for (int y = 0; y < height; ++y) {
            StringBuilder spec = new StringBuilder();
            for (int x = 0; x < width; ++x, ++index) {
                if (input[index] != null) {
                    spec.append((char) (index + 'A'));
                    recipe[_i++] = (char) (index + 'A');
                    recipe[_i++] = input[index];
                } else {
                    spec.append(' ');
                }
            }
            recipe[y] = spec.toString();
        }

        Debug.debug("[ShapedOre] " +
                RecipeRegistry.reprStack(output) + "[" + mirrored + "]" +
                Joiner.on(',').join(recipe));
        GameRegistry.addShapedRecipe(new ResourceLocation(registry.PREFIX + ':' + UUID.randomUUID()),
                new ResourceLocation(registry.PREFIX), output, mirrored, recipe);
    }

}
