package cn.academy.internal.item;

import cn.academy.internal.energy.api.IFItemManager;
import cn.academy.internal.energy.api.item.ImagEnergyItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author WeAthFolD
 */
public class ItemEnergyBase extends Item implements ImagEnergyItem {
    protected static IFItemManager itemManager = IFItemManager.instance;

    public final double maxEnergy;
    public final double bandwidth;

    public ItemEnergyBase(double _maxEnergy, double _bandwidth) {
        maxEnergy = _maxEnergy;
        bandwidth = _bandwidth;

        setMaxStackSize(1);
        setMaxDamage(13);
        addPropertyOverride(new ResourceLocation("energy"), (stack, worldIn, entityIn) -> {
            int damage = stack.getItemDamage();
            if (damage < 3)
                return 1.0f;
            if (damage > 10)
                return 0.0f;
            return 0.5f;
        });
    }

    @Override
    public double getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public double getBandwidth() {
        return bandwidth;
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            ItemStack is = new ItemStack(this);
            items.add(is);
            itemManager.charge(is, 0, true);

            is = new ItemStack(this);
            itemManager.charge(is, Double.MAX_VALUE, true);
            items.add(is);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(itemManager.getDescription(stack));
    }
}