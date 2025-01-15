package cn.academy.internal.item;

import cn.academy.AcademyCraftBlockList;
import net.minecraft.item.ItemBucket;

/**
 * The matter unit class. Have a simple material system for registration.
 * @author WeAthFolD
 */
public class ItemMatterUnitPhaseLiquid extends ItemBucket {
    public ItemMatterUnitPhaseLiquid() {
        super(AcademyCraftBlockList.IMAG_PHASE);
        this.setMaxStackSize(16);
    }
}