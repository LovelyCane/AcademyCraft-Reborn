package cn.academy.internal.ability.develop;

import cn.academy.AcademyCraftItemList;
import cn.academy.internal.energy.api.IFItemManager;
import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * The Developer instance for portable developer attached one per player.
 * @author WeAthFolD
 */
@RegDataPart(EntityPlayer.class)
public class PortableDevData extends DataPart<EntityPlayer> implements IDeveloper {
    public static PortableDevData get(EntityPlayer player) {
        return EntityData.get(player).getPart(PortableDevData.class);
    }

    private ItemStack stack() {
        ItemStack stack = getEntity().getHeldItemMainhand();
        return stack.getItem() == AcademyCraftItemList.DEVELOPER_PORTABLE ? stack : null;
    }

    @Override
    public DeveloperType getType() {
        return DeveloperType.PORTABLE;
    }

    @Override
    public boolean tryPullEnergy(double amount) {
        ItemStack stack = stack();
        if(stack == null)
            return false;
        return IFItemManager.instance.pull(stack, amount, true) == amount;
    }

    @Override
    public double getEnergy() {
        ItemStack stack = stack();
        return stack == null ? 0 : IFItemManager.instance.getEnergy(stack);
    }

    @Override
    public double getMaxEnergy() {
        ItemStack stack = stack();
        return stack == null ? 0 : IFItemManager.instance.getMaxEnergy(stack);
    }
}