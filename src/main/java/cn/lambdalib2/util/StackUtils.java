/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class StackUtils {

    public static void dropItems(World world, BlockPos pos,
                                 IInventory inv) {
        Random rand = new Random();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getCount() > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                    pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, stack.copy());

                if (stack.hasTagCompound()) {
                    entityItem.getItem().setTagCompound(
                            (NBTTagCompound) stack.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntity(entityItem);
                stack.setCount(0);
            }
        }
    }

    /**
     * Return whether two stack's item instance, item damage and data are equal.
     */
    public static boolean isStackDataEqual(ItemStack s1, ItemStack s2) {
        if (s1.getItem() != s2.getItem() || s1.getItemDamage() != s2.getItemDamage())
            return false;
        NBTTagCompound tag1 = s1.getTagCompound(), tag2 = s2.getTagCompound();
        if (tag1 == null || tag2 == null) {
            return tag1 == null && tag2 == null;
        }

        return tag1.equals(tag2);
    }

    public static NBTTagCompound loadTag(ItemStack stack) {
        NBTTagCompound ret = stack.getTagCompound();
        if (ret == null) {
            ret = new NBTTagCompound();
            stack.setTagCompound(ret);
        }
        return ret;
    }

}
