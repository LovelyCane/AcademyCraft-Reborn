package cn.academy.internal.ability.develop.action;

import cn.academy.api.ability.Category;
import cn.academy.internal.ability.develop.IDeveloper;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.event.ability.TransformCategoryEvent;
import cn.academy.internal.item.ItemInductionFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class DevelopActionReset implements IDevelopAction {

    public static boolean canReset(EntityPlayer player, IDeveloper developer) {
        return true;
    }

    static Optional<ItemStack> getFactor(EntityPlayer player) {
        Category playerCategory = AbilityData.get(player).getCategoryNullable();
        return player.inventory.mainInventory.parallelStream()
                .filter(stack -> (!stack.isEmpty()) && stack.getItem() instanceof ItemInductionFactor)
                .filter(stack -> ItemInductionFactor.getCategory(stack) != playerCategory)
                .findAny();
    }

    @Override
    public int getStimulations(EntityPlayer player) {
        AbilityData data = AbilityData.get(player);
        return data.getLevel() * 10;
    }

    @Override
    public boolean validate(EntityPlayer player, IDeveloper developer) {
        return canReset(player, developer);
    }

    @Override
    public void onLearned(EntityPlayer player) {
        AbilityData data = AbilityData.get(player);

        ItemStack factor = getFactor(player).get();

        Category newCat = ItemInductionFactor.getCategory(factor);

        int prevLevel = data.getLevel() - 1;
        if(!MinecraftForge.EVENT_BUS.post(new TransformCategoryEvent(player, newCat, prevLevel)))
        {
            data.setCategory(newCat);
            data.setLevel(prevLevel);

            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

            int factorIdx = player.inventory.mainInventory.indexOf(factor);
            player.inventory.mainInventory.set(factorIdx, ItemStack.EMPTY);
        }
    }
}