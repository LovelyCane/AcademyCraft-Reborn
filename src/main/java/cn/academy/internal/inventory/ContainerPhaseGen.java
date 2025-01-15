package cn.academy.internal.inventory;

import cn.academy.AcademyCraftItemList;
import cn.academy.internal.energy.api.IFItemManager;
import cn.academy.internal.tileentity.TilePhaseGen;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author WeAthFolD
 */
public class ContainerPhaseGen extends TechUIContainer<TilePhaseGen> {
    public static final int SLOT_LIQUID_IN = 0, SLOT_LIQUID_OUT = 1, SLOT_OUTPUT = 2;

    public ContainerPhaseGen(EntityPlayer _player, TilePhaseGen _tile) {
        super(_player, _tile);

        initInventory();
    }

    private void initInventory() {
        this.addSlotToContainer(new SlotMatterUnit(tile, false, SLOT_LIQUID_IN, 45, 12));
        this.addSlotToContainer(new SlotMatterUnit(tile, true, SLOT_LIQUID_OUT, 112, 51));
        this.addSlotToContainer(new SlotIFItem(tile, SLOT_OUTPUT, 42, 80));

        mapPlayerInventory();

        SlotGroup gMachine = gRange(0, 3);
        SlotGroup gInv = gRange(4, 4+36);

        addTransferRule(gMachine, gInv);
        addTransferRule(gInv, IFItemManager.instance::isSupported, gSlots(SLOT_OUTPUT));
        addTransferRule(gInv, stack -> stack == AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID.getContainerItem(stack), gSlots(SLOT_LIQUID_IN));
    }
}