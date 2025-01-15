package cn.academy;

import cn.academy.internal.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.ArrayList;
import java.util.List;

public class AcademyCraftItemList {
    public static final List<Item> ITEM_LIST = new ArrayList<>();
    public static final ItemBlock ITEM_CAT_ENGINE = new ItemBlock(AcademyCraftBlockList.CAT_ENGINE);
    public static final ItemBlock ITEM_CONSTRAINT_METAL = new ItemBlock(AcademyCraftBlockList.CONSTRAINT_METAL);
    public static final ItemBlock ITEM_CRYSTAL_ORE = new ItemBlock(AcademyCraftBlockList.CRYSTAL_ORE);
    public static final ItemBlock ITEM_IMAG_FUSOR = new ItemBlock(AcademyCraftBlockList.IMAG_FUSOR);
    public static final ItemBlock ITEM_IMAG_PHASE = new ItemBlock(AcademyCraftBlockList.IMAG_PHASE);
    public static final ItemBlock ITEM_IMAGSIL_ORE = new ItemBlock(AcademyCraftBlockList.IMAGSIL_ORE);
    public static final ItemBlock ITEM_MACHINE_FRAME = new ItemBlock(AcademyCraftBlockList.MACHINE_FRAME);
    public static final ItemBlock ITEM_METAL_FORMER = new ItemBlock(AcademyCraftBlockList.METAL_FORMER);
    public static final ItemBlock ITEM_NODE_ADVANCED = new ItemBlock(AcademyCraftBlockList.NODE_ADVANCED);
    public static final ItemBlock ITEM_NODE_BASIC = new ItemBlock(AcademyCraftBlockList.NODE_BASIC);
    public static final ItemBlock ITEM_NODE_STANDARD = new ItemBlock(AcademyCraftBlockList.NODE_STANDARD);
    public static final ItemBlock ITEM_PHASE_GEN = new ItemBlock(AcademyCraftBlockList.PHASE_GEN);
    public static final ItemBlock ITEM_RESO_ORE = new ItemBlock(AcademyCraftBlockList.RESO_ORE);
    public static final ItemBlock ITEM_SOLAR_GEN = new ItemBlock(AcademyCraftBlockList.SOLAR_GEN);
    public static final ItemApp APP_FREQ_TRANSMITTER = new ItemApp("freq_transmitter");
    public static final ItemApp APP_MEDIA_PLAYER = new ItemApp("media_player");
    public static final ItemApp APP_SKILL_TREE = new ItemApp("skill_tree");
    public static final net.minecraft.item.Item BRAIN_COMPONENT = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item CALC_CHIP = new net.minecraft.item.Item();
    public static final ItemCoin COIN = new ItemCoin();
    public static final net.minecraft.item.Item CONSTRAINT_INGOT = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item CONSTRAINT_PLATE = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item CRYSTAL_LOW = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item CRYSTAL_NORMAL = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item CRYSTAL_PURE = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item DATA_CHIP = new net.minecraft.item.Item();
    public static final ItemDeveloperPortable DEVELOPER_PORTABLE = new ItemDeveloperPortable();
    public static final net.minecraft.item.Item ENERGY_CONVERT_COMPONENT = new net.minecraft.item.Item();
    public static final ItemEnergyBase ENERGY_UNIT = new ItemEnergyBase(10000, 20);
    public static final net.minecraft.item.Item IMAG_SILICON_INGOT = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item IMAG_SILICON_PIECE = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item INFO_COMPONENT = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item LOGO = new net.minecraft.item.Item();
    public static final ItemMagHook MAG_HOOK = new ItemMagHook();
    public static final ItemMagneticCoil MAGNETIC_COIL = new ItemMagneticCoil();
    public static final ItemEmptyMatterUnit EMPTY_MATTER_UNIT = new ItemEmptyMatterUnit();
    public static final ItemMatterUnitPhaseLiquid MATTER_UNIT_PHASE_LIQUID = new ItemMatterUnitPhaseLiquid();
    public static final net.minecraft.item.Item NEEDLE = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item REINFORCED_IRON_PLATE = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item RESO_CRYSTAL = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item RESONANCE_COMPONENT = new net.minecraft.item.Item();
    public static final ItemSilbarn SILBARN = new ItemSilbarn();
    public static final ItemTerminalInstaller TERMINAL_INSTALLER = new ItemTerminalInstaller();
    public static final net.minecraft.item.Item WAFER = new net.minecraft.item.Item();

    static {
        AcademyCraftItemList.APP_FREQ_TRANSMITTER.setRegistryName("academy:app_freq_transmitter");
        AcademyCraftItemList.APP_FREQ_TRANSMITTER.setTranslationKey("ac_apps");
        AcademyCraftItemList.APP_FREQ_TRANSMITTER.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.APP_FREQ_TRANSMITTER);

        AcademyCraftItemList.APP_MEDIA_PLAYER.setRegistryName("academy:app_media_player");
        AcademyCraftItemList.APP_MEDIA_PLAYER.setTranslationKey("ac_apps");
        AcademyCraftItemList.APP_MEDIA_PLAYER.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.APP_MEDIA_PLAYER);

        AcademyCraftItemList.APP_SKILL_TREE.setRegistryName("academy:app_skill_tree");
        AcademyCraftItemList.APP_SKILL_TREE.setTranslationKey("ac_apps");
        AcademyCraftItemList.APP_SKILL_TREE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.APP_SKILL_TREE);

        AcademyCraftItemList.BRAIN_COMPONENT.setRegistryName("academy:brain_component");
        AcademyCraftItemList.BRAIN_COMPONENT.setTranslationKey("ac_brain_component");
        AcademyCraftItemList.BRAIN_COMPONENT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.BRAIN_COMPONENT);

        AcademyCraftItemList.CALC_CHIP.setRegistryName("academy:calc_chip");
        AcademyCraftItemList.CALC_CHIP.setTranslationKey("ac_calc_chip");
        AcademyCraftItemList.CALC_CHIP.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CALC_CHIP);

        AcademyCraftItemList.COIN.setRegistryName("academy:coin");
        AcademyCraftItemList.COIN.setTranslationKey("ac_coin");
        AcademyCraftItemList.COIN.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.COIN);

        AcademyCraftItemList.CONSTRAINT_INGOT.setRegistryName("academy:constraint_ingot");
        AcademyCraftItemList.CONSTRAINT_INGOT.setTranslationKey("ac_constraint_ingot");
        AcademyCraftItemList.CONSTRAINT_INGOT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CONSTRAINT_INGOT);

        AcademyCraftItemList.CONSTRAINT_PLATE.setRegistryName("academy:constraint_plate");
        AcademyCraftItemList.CONSTRAINT_PLATE.setTranslationKey("ac_constraint_plate");
        AcademyCraftItemList.CONSTRAINT_PLATE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CONSTRAINT_PLATE);

        AcademyCraftItemList.CRYSTAL_LOW.setRegistryName("academy:crystal_low");
        AcademyCraftItemList.CRYSTAL_LOW.setTranslationKey("ac_crystal_low");
        AcademyCraftItemList.CRYSTAL_LOW.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CRYSTAL_LOW);

        AcademyCraftItemList.CRYSTAL_NORMAL.setRegistryName("academy:crystal_normal");
        AcademyCraftItemList.CRYSTAL_NORMAL.setTranslationKey("ac_crystal_normal");
        AcademyCraftItemList.CRYSTAL_NORMAL.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CRYSTAL_NORMAL);

        AcademyCraftItemList.CRYSTAL_PURE.setRegistryName("academy:crystal_pure");
        AcademyCraftItemList.CRYSTAL_PURE.setTranslationKey("ac_crystal_pure");
        AcademyCraftItemList.CRYSTAL_PURE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.CRYSTAL_PURE);

        AcademyCraftItemList.DATA_CHIP.setRegistryName("academy:data_chip");
        AcademyCraftItemList.DATA_CHIP.setTranslationKey("ac_data_chip");
        AcademyCraftItemList.DATA_CHIP.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.DATA_CHIP);

        AcademyCraftItemList.DEVELOPER_PORTABLE.setRegistryName("academy:developer_portable");
        AcademyCraftItemList.DEVELOPER_PORTABLE.setTranslationKey("ac_developer_portable");
        AcademyCraftItemList.DEVELOPER_PORTABLE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.DEVELOPER_PORTABLE);
        AcademyCraftItemList.DEVELOPER_PORTABLE.afterRegistry();

        AcademyCraftItemList.ENERGY_CONVERT_COMPONENT.setRegistryName("academy:energy_convert_component");
        AcademyCraftItemList.ENERGY_CONVERT_COMPONENT.setTranslationKey("ac_energy_convert_component");
        AcademyCraftItemList.ENERGY_CONVERT_COMPONENT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.ENERGY_CONVERT_COMPONENT);

        AcademyCraftItemList.ENERGY_UNIT.setRegistryName("academy:energy_unit");
        AcademyCraftItemList.ENERGY_UNIT.setTranslationKey("ac_energy_unit");
        AcademyCraftItemList.ENERGY_UNIT.setNoRepair();
        AcademyCraftItemList.ENERGY_UNIT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.ENERGY_UNIT);

        AcademyCraftItemList.IMAG_SILICON_INGOT.setRegistryName("academy:imag_silicon_ingot");
        AcademyCraftItemList.IMAG_SILICON_INGOT.setTranslationKey("ac_imag_silicon_ingot");
        AcademyCraftItemList.IMAG_SILICON_INGOT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.IMAG_SILICON_INGOT);

        AcademyCraftItemList.IMAG_SILICON_PIECE.setRegistryName("academy:imag_silicon_piece");
        AcademyCraftItemList.IMAG_SILICON_PIECE.setTranslationKey("ac_imag_silicon_piece");
        AcademyCraftItemList.IMAG_SILICON_PIECE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.IMAG_SILICON_PIECE);

        AcademyCraftItemList.INFO_COMPONENT.setRegistryName("academy:info_component");
        AcademyCraftItemList.INFO_COMPONENT.setTranslationKey("ac_info_component");
        AcademyCraftItemList.INFO_COMPONENT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.INFO_COMPONENT);

        AcademyCraftItemList.LOGO.setRegistryName("academy:logo");
        AcademyCraftItemList.LOGO.setTranslationKey("ac_logo");
        ITEM_LIST.add(AcademyCraftItemList.LOGO);

        AcademyCraftItemList.MAG_HOOK.setRegistryName("academy:mag_hook");
        AcademyCraftItemList.MAG_HOOK.setTranslationKey("ac_mag_hook");
        AcademyCraftItemList.MAG_HOOK.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.MAG_HOOK);
        AcademyCraftItemList.MAG_HOOK.afterRegistry();

        AcademyCraftItemList.MAGNETIC_COIL.setRegistryName("academy:magnetic_coil");
        AcademyCraftItemList.MAGNETIC_COIL.setTranslationKey("ac_magnetic_coil");
        AcademyCraftItemList.MAGNETIC_COIL.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.MAGNETIC_COIL);

        AcademyCraftItemList.EMPTY_MATTER_UNIT.setRegistryName("academy:empty_matter_unit");
        AcademyCraftItemList.EMPTY_MATTER_UNIT.setTranslationKey("ac_empty_matter_unit");
        AcademyCraftItemList.EMPTY_MATTER_UNIT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.EMPTY_MATTER_UNIT);

        AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID.setRegistryName("academy:matter_unit_phase_liquid");
        AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID.setTranslationKey("ac_matter_unit_phase_liquid");
        AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.MATTER_UNIT_PHASE_LIQUID);

        AcademyCraftItemList.NEEDLE.setRegistryName("academy:needle");
        AcademyCraftItemList.NEEDLE.setTranslationKey("ac_needle");
        AcademyCraftItemList.NEEDLE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.NEEDLE);

        AcademyCraftItemList.REINFORCED_IRON_PLATE.setRegistryName("academy:reinforced_iron_plate");
        AcademyCraftItemList.REINFORCED_IRON_PLATE.setTranslationKey("ac_reinforced_iron_plate");
        AcademyCraftItemList.REINFORCED_IRON_PLATE.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.REINFORCED_IRON_PLATE);

        AcademyCraftItemList.RESO_CRYSTAL.setRegistryName("academy:reso_crystal");
        AcademyCraftItemList.RESO_CRYSTAL.setTranslationKey("ac_reso_crystal");
        AcademyCraftItemList.RESO_CRYSTAL.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.RESO_CRYSTAL);

        AcademyCraftItemList.RESONANCE_COMPONENT.setRegistryName("academy:resonance_component");
        AcademyCraftItemList.RESONANCE_COMPONENT.setTranslationKey("ac_resonance_component");
        AcademyCraftItemList.RESONANCE_COMPONENT.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.RESONANCE_COMPONENT);

        AcademyCraftItemList.SILBARN.setRegistryName("academy:silbarn");
        AcademyCraftItemList.SILBARN.setTranslationKey("ac_silbarn");
        AcademyCraftItemList.SILBARN.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.SILBARN);
        AcademyCraftItemList.SILBARN.afterRegistry();

        AcademyCraftItemList.TERMINAL_INSTALLER.setRegistryName("academy:terminal_installer");
        AcademyCraftItemList.TERMINAL_INSTALLER.setTranslationKey("ac_terminal_installer");
        AcademyCraftItemList.TERMINAL_INSTALLER.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.TERMINAL_INSTALLER);

        AcademyCraftItemList.WAFER.setRegistryName("academy:wafer");
        AcademyCraftItemList.WAFER.setTranslationKey("ac_wafer");
        AcademyCraftItemList.WAFER.setCreativeTab(cn.academy.AcademyCraft.cct);
        ITEM_LIST.add(AcademyCraftItemList.WAFER);

        AcademyCraftItemList.ITEM_CAT_ENGINE.setRegistryName("academy:cat_engine");
        AcademyCraftItemList.ITEM_CAT_ENGINE.setTranslationKey("ac_cat_engine");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_CAT_ENGINE);

        AcademyCraftItemList.ITEM_CONSTRAINT_METAL.setRegistryName("academy:constraint_metal");
        AcademyCraftItemList.ITEM_CONSTRAINT_METAL.setTranslationKey("ac_constraint_metal");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_CONSTRAINT_METAL);

        AcademyCraftItemList.ITEM_CRYSTAL_ORE.setRegistryName("academy:crystal_ore");
        AcademyCraftItemList.ITEM_CRYSTAL_ORE.setTranslationKey("ac_crystal_ore");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_CRYSTAL_ORE);

        AcademyCraftItemList.ITEM_IMAG_FUSOR.setRegistryName("academy:imag_fusor");
        AcademyCraftItemList.ITEM_IMAG_FUSOR.setTranslationKey("ac_imag_fusor");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_IMAG_FUSOR);

        AcademyCraftItemList.ITEM_IMAG_PHASE.setRegistryName("academy:imag_phase");
        AcademyCraftItemList.ITEM_IMAG_PHASE.setTranslationKey("ac_imag_phase");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_IMAG_PHASE);

        AcademyCraftItemList.ITEM_IMAGSIL_ORE.setRegistryName("academy:imagsil_ore");
        AcademyCraftItemList.ITEM_IMAGSIL_ORE.setTranslationKey("ac_imagsil_ore");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_IMAGSIL_ORE);

        AcademyCraftItemList.ITEM_MACHINE_FRAME.setRegistryName("academy:machine_frame");
        AcademyCraftItemList.ITEM_MACHINE_FRAME.setTranslationKey("ac_machine_frame");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_MACHINE_FRAME);

        AcademyCraftItemList.ITEM_METAL_FORMER.setRegistryName("academy:metal_former");
        AcademyCraftItemList.ITEM_METAL_FORMER.setTranslationKey("ac_metal_former");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_METAL_FORMER);

        AcademyCraftItemList.ITEM_NODE_ADVANCED.setRegistryName("academy:node_advanced");
        AcademyCraftItemList.ITEM_NODE_ADVANCED.setTranslationKey("ac_node_advanced");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_NODE_ADVANCED);

        AcademyCraftItemList.ITEM_NODE_BASIC.setRegistryName("academy:node_basic");
        AcademyCraftItemList.ITEM_NODE_BASIC.setTranslationKey("ac_node_basic");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_NODE_BASIC);

        AcademyCraftItemList.ITEM_NODE_STANDARD.setRegistryName("academy:node_standard");
        AcademyCraftItemList.ITEM_NODE_STANDARD.setTranslationKey("ac_node_standard");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_NODE_STANDARD);

        AcademyCraftItemList.ITEM_PHASE_GEN.setRegistryName("academy:phase_gen");
        AcademyCraftItemList.ITEM_PHASE_GEN.setTranslationKey("ac_phase_gen");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_PHASE_GEN);

        AcademyCraftItemList.ITEM_RESO_ORE.setRegistryName("academy:reso_ore");
        AcademyCraftItemList.ITEM_RESO_ORE.setTranslationKey("ac_reso_ore");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_RESO_ORE);

        AcademyCraftItemList.ITEM_SOLAR_GEN.setRegistryName("academy:solar_gen");
        AcademyCraftItemList.ITEM_SOLAR_GEN.setTranslationKey("ac_solar_gen");
        ITEM_LIST.add(AcademyCraftItemList.ITEM_SOLAR_GEN);
    }

    private AcademyCraftItemList() {
    }
}
