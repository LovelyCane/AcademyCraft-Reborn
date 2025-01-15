package cn.academy.internal.support.jei;

import cn.academy.AcademyCraftBlockList;
import cn.academy.internal.crafting.ImagFusorRecipes;
import cn.academy.internal.crafting.ImagFusorRecipes.IFRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author KSkun
 */
public class FusorRecipeCategory extends IACRecipeCategory {
    public static List<IRecipeWrapper> recipeWrapper = loadCraftingRecipes();
    private static final ResourceLocation bg = new ResourceLocation("academy", "textures/guis/nei_fusor.png");
    private final IGuiHelper guiHelper;

    public FusorRecipeCategory(IGuiHelper guiHelper) {
        super(AcademyCraftBlockList.IMAG_FUSOR);
        this.guiHelper = guiHelper;
    }

    @Override
    public String getTitle() {
        return I18n.format("tile.ac_imag_fusor.name");
    }

    //TODO 物品槽的具体位置还需要调整
    @Override
    public List<SlotPos> getInputs() {
        return Collections.singletonList(new SlotPos(5, 36));
    }


    @Override
    public List<SlotPos> getOutputs() {
        return Collections.singletonList(new SlotPos(93, 36));
    }

    private static List<IRecipeWrapper> loadCraftingRecipes() {
        List<IRecipeWrapper> lists = new ArrayList<>();
        for (IFRecipe r : ImagFusorRecipes.INSTANCE.getAllRecipe()) {
            lists.add(iIngredients -> {
                iIngredients.setInput(ItemStack.class, r.consumeType);
                iIngredients.setOutput(ItemStack.class, r.output);
            });
        }
        return lists;
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(bg, 0, 0, 115, 66, 115, 66);
    }
}