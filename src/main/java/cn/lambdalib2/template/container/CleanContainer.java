package cn.lambdalib2.template.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class wraps around vanilla MC's {@link Container}, making the dirty part somehow more usable -- Namely, the
 *  ItemStack transfering part. The original version is verbose at use site and its intension is very unclear, so it's
 *  replaced by the neat {@link #addTransferRule(SlotGroup, Predicate, SlotGroup)} interface.
 */
public abstract class CleanContainer extends Container {
    /**
     * @return A slot group containing specified slots
     */
    public static SlotGroup gSlots(int ...slots) {
        return new SlotGroup(slots);
    }

    /**
     * @return A slot group containing specified range of slots
     */
    public static SlotGroup gRange(int from, int toExclusive) {
        return new SlotGroup(from, toExclusive);
    }

    private final List<Rule> rules = new ArrayList<>();

    /**
     * Adds a stack transfer rule.
     * @param fromSlots Slots that accepts this rule
     * @param inPredicate Tests stacks in from slot, only those passed will be transfered
     * @param toSlots Slot id to transfer stack to
     */
    public void addTransferRule(SlotGroup fromSlots, Predicate<ItemStack> inPredicate, SlotGroup toSlots) {
        Rule rule = new Rule();
        rule.from = fromSlots;
        rule.pred = inPredicate;
        rule.to = toSlots;
        rules.add(rule);
    }

    public void addTransferRule(SlotGroup from, SlotGroup to) {
        addTransferRule(from, s -> true, to);
    }

    @Override
    public final ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        final Slot slot = getSlot(slotID);
        if (slot.getHasStack()) {
            final ItemStack slotStack = slot.getStack();
            final ItemStack stackCopy = slotStack.copy();

            boolean slotChanged = false;
            for (Rule rule : rules) {
                if (rule.from.accepts(slotID) && rule.pred.test(slotStack)) {

                    for (int toSlot : rule.to.slots) {
                        if (slotStack.getCount() <= 0)
                            break;

                        slotChanged |= tryMerge(slotStack, toSlot);
                    }

                    break;
                }
            }

            if (slotStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else if (slotChanged) {
                slot.onSlotChanged();
            }

            return slotChanged ? stackCopy : ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Put item(stack) to slot[idx].
     * If slot is empty, put it.
     * Then check if they can be merged.
     * @param stackToMerge slot merged from
     * @param idx slot merged to
     */
    private boolean tryMerge(ItemStack stackToMerge, int idx) {
        Slot slot = this.inventorySlots.get(idx);
        ItemStack stack = slot.getStack();

        if (stack.isEmpty() && slot.isItemValid(stackToMerge)) {
            stack = stackToMerge.copy();
            stack.setCount(1);
            stackToMerge.setCount(stackToMerge.getCount() - 1);

            slot.putStack(stack);
            slot.onSlotChanged();
        }

        if (!stack.isEmpty() &&
                stack.getItem() == stackToMerge.getItem() &&
                (!stackToMerge.getHasSubtypes() ||
                        stackToMerge.getItemDamage() == stack.getItemDamage()) &&
                ItemStack.areItemStackTagsEqual(stackToMerge, stack)) {
            int ideal = stack.getCount() + stackToMerge.getCount();
            int max = Math.min(stackToMerge.getMaxStackSize(), slot.getSlotStackLimit());
            if(ideal <= max) {
                stackToMerge.setCount(0);
                stack.setCount(ideal);
                slot.onSlotChanged();
                return true;
            } else if(stack.getCount() < max) {
                stackToMerge.shrink( max - stack.getCount() );
                stack.setCount(max);
                slot.onSlotChanged();
                return true;
            }
        }
        return false;
    }

    protected static class SlotGroup {

        final int[] slots;

        private SlotGroup(int[] slots) {
            this.slots = slots;
        }

        private SlotGroup(int from, int toExclusive) {
            this.slots = new int[toExclusive - from];
            for (int i = from; i < toExclusive; ++i) {
                slots[i - from] = i;
            }
        }

        private boolean accepts(int slot) {
            for (int i : slots) {
                if (i == slot) return true;
            }
            return false;
        }

    }

    private static class Rule {
        SlotGroup from, to;
        Predicate<ItemStack> pred;
    }
}
