package cn.academy.internal.inventory;

import cn.academy.AcademyCraftItemList;
import cn.academy.internal.crafting.ImagFusorRecipes;
import cn.academy.internal.energy.api.IFItemManager;
import cn.academy.internal.item.ItemMatterUnitPhaseLiquid;
import cn.academy.internal.tileentity.TileImagFusor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static cn.academy.internal.tileentity.TileImagFusor.*;

/**
 * @author WeAthFolD
 */
public class ContainerImagFusor extends TechUIContainer<TileImagFusor> {

    public ContainerImagFusor(TileImagFusor _tile, EntityPlayer _player) {
        super(_player, _tile);

        initInventory();
    }

    private void initInventory() {
        this.addSlotToContainer(new SlotCrystal(tile, SLOT_INPUT, 13, 49));
        this.addSlotToContainer(new SlotCrystal(tile, SLOT_OUTPUT, 143, 49));
        this.addSlotToContainer(new SlotMatterUnit(tile, false, SLOT_IMAG_INPUT, 13, 10));
        this.addSlotToContainer(new SlotMatterUnit(tile, true, SLOT_IMAG_OUTPUT, 143, 10));
        this.addSlotToContainer(new SlotIFItem(tile, SLOT_ENERGY_INPUT, 42, 80));

        mapPlayerInventory();

        ItemMatterUnitPhaseLiquid unit = AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID;
        SlotGroup inventoryGroup = gRange(4, inventorySlots.size());

        this.addTransferRule(inventoryGroup, stack -> stack == AcademyCraftItemList.EMPTY_MATTER_UNIT.getContainerItem(stack), gSlots(2));

        this.addTransferRule(inventoryGroup, stack -> IFItemManager.instance.isSupported(stack), gSlots(3));

        this.addTransferRule(inventoryGroup, gSlots(0));

        this.addTransferRule(gRange(0, 4), inventoryGroup);
    }

    /**
     * @author KSkun
     */
    private static class SlotCrystal extends Slot {

        private final int slot;

        public SlotCrystal(IInventory inv, int _slot, int x, int y) {
            super(inv, _slot, x, y);
            slot = _slot;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (slot == 0) {
                for (ImagFusorRecipes.IFRecipe obj : ImagFusorRecipes.INSTANCE.getAllRecipe()) {
                    if (obj.consumeType.getItem() == stack.getItem())
                        return true;
                }
            }
            return false;
        }

    }
}