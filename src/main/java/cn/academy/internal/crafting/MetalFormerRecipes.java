package cn.academy.internal.crafting;

import cn.academy.internal.tileentity.TileMetalFormer.Mode;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeAthFolD
 */
public enum MetalFormerRecipes {
    INSTANCE;

    public static class RecipeObject {
        public int id = -1;

        public final Mode mode;
        public final ItemStack input;
        public final ItemStack output;

        private RecipeObject(ItemStack _input, ItemStack _output, Mode _mode) {
            input = _input;
            output = _output;
            mode = _mode;
        }

        public boolean accepts(ItemStack stack, Mode mode2) {
            return stack != null &&
                    mode == mode2 &&
                    input.getItem() == stack.getItem() &&
                    input.getCount() <= stack.getCount() &&
                    input.getItemDamage() == stack.getItemDamage();
        }
    }

    public final List<RecipeObject> objects = new ArrayList<>();

    public void add(ItemStack in, ItemStack out, Mode mode) {
        RecipeObject add = new RecipeObject(in, out, mode);
        add.id = objects.size();
        objects.add(add);
    }

    public RecipeObject getRecipe(ItemStack input, Mode mode) {
        for (RecipeObject recipe : objects) {
            if (recipe.accepts(input, mode))
                return recipe;
        }
        return null;
    }

    public List<RecipeObject> getAllRecipes() {
        return objects;
    }
}