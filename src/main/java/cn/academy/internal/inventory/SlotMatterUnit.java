package cn.academy.internal.inventory;

import cn.academy.AcademyCraftItemList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author WeAthFolD
 */
public class SlotMatterUnit extends Slot {
    boolean empty;

    public SlotMatterUnit(IInventory inv, boolean empty, int slot, int x, int y) {
        super(inv, slot, x, y);
        this.empty = empty;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == (empty ? AcademyCraftItemList.EMPTY_MATTER_UNIT : AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID);
    }
}