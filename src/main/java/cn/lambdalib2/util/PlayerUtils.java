/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
//import net.minecraft.util.ChatComponentTranslation;

/**
 * Utils that are built around a player.
 * @author WeAthFolD
 */
public class PlayerUtils {
    
    /**
     * Try to merge an itemStack into the player inventory. The merging uses strict
     * equality, that is, only when: <br>
     *  * item instances are equal <br>
     *  * NBT datas are equal <br>
     *  * Damage values are equal <br>
     * can this stack merge to an another stack in the inventory.
     * @return The stack size that is not merged into the inventory.
     */
    public static int mergeStackable(InventoryPlayer inv, ItemStack stack) {
        for(int i = 0; i < inv.getSizeInventory() - 4 && stack.getCount() > 0; ++i) {
            ItemStack is = inv.getStackInSlot(i);
            if(is != null && StackUtils.isStackDataEqual(stack, is) && is.getItemDamage() == stack.getItemDamage()) {
                is.grow(stack.getCount());
                int left = Math.max(0, is.getCount() - is.getMaxStackSize());
                stack.setCount(left);
                is.shrink(left);
            }
        }
        if(stack.getCount() > 0) {
            int id = inv.getFirstEmptyStack();
            if(id == -1) {
                return stack.getCount();
            }
            inv.setInventorySlotContents(id, stack.copy());
            return 0;
        }
        return 0;
    }
    
    /**
     * Try to find the index of a item in player's inventory. if fail, return -1.
     */
    public static int getSlotByStack(ItemStack item, EntityPlayer player) {
        return player.inventory.getSlotFor(item);
//        InventoryPlayer inv = player.inventory;
//        for(int i = 0; i < inv.mainInventory.length; i++) {
//            ItemStack is = inv.mainInventory[i];
//            if(is != null && item == is)
//                return i;
//        }
//        return -1;
    }
    
    /**
     * Abbr for annoying addChatMessage(new ChatComponentTranslation(...)).
     * @param ics Message sending target
     * @param message Message
     * @param pars Message parameters
     */
    public static void sendChat(ICommandSender ics, String message, Object... pars) {
//        ics.addChatMessage(new ChatComponentTranslation(message, (Object[]) pars));
        ics.sendMessage(new TextComponentTranslation(message, pars));
    }

    
}
