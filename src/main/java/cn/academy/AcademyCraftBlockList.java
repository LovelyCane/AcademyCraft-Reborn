package cn.academy;

import cn.academy.internal.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class AcademyCraftBlockList {
    public static final List<Block> BLOCK_LIST = new ArrayList<>();
    public static final BlockCatEngine CAT_ENGINE = new BlockCatEngine();
    public static final BlockGenericOre CONSTRAINT_METAL = new BlockGenericOre(4.0f, 1);
    public static final BlockGenericOre CRYSTAL_ORE = new BlockGenericOre(3.0f, 2);
    public static final BlockImagFusor IMAG_FUSOR = new BlockImagFusor();
    public static final BlockImagPhase IMAG_PHASE = new BlockImagPhase();
    public static final BlockGenericOre IMAGSIL_ORE = new BlockGenericOre(3.75f, 2);
    public static final Block MACHINE_FRAME = new net.minecraft.block.Block(Material.ROCK);
    public static final BlockMetalFormer METAL_FORMER = new BlockMetalFormer();
    public static final BlockNode NODE_ADVANCED = new BlockNode(BlockNode.NodeType.ADVANCED);
    public static final BlockNode NODE_BASIC = new BlockNode(BlockNode.NodeType.BASIC);
    public static final BlockNode NODE_STANDARD = new BlockNode(BlockNode.NodeType.STANDARD);
    public static final BlockPhaseGen PHASE_GEN = new BlockPhaseGen();
    public static final BlockGenericOre RESO_ORE = new BlockGenericOre(3f, 2);
    public static final BlockSolarGen SOLAR_GEN = new BlockSolarGen();

    static {
        CAT_ENGINE.setRegistryName("academy:cat_engine");
        CAT_ENGINE.setTranslationKey("ac_cat_engine");
        CAT_ENGINE.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(CAT_ENGINE);
        CONSTRAINT_METAL.setRegistryName("academy:constraint_metal");
        CONSTRAINT_METAL.setTranslationKey("ac_constraint_metal");
        CONSTRAINT_METAL.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(CONSTRAINT_METAL);
        CRYSTAL_ORE.setRegistryName("academy:crystal_ore");
        CRYSTAL_ORE.setTranslationKey("ac_crystal_ore");
        CRYSTAL_ORE.setCreativeTab(cn.academy.AcademyCraft.cct);
        CRYSTAL_ORE.setDropData(AcademyCraftItemList.CRYSTAL_LOW, 1, 3);
        BLOCK_LIST.add(CRYSTAL_ORE);
        IMAG_FUSOR.setRegistryName("academy:imag_fusor");
        IMAG_FUSOR.setTranslationKey("ac_imag_fusor");
        IMAG_FUSOR.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(IMAG_FUSOR);
        IMAG_PHASE.setRegistryName("academy:imag_phase");
        IMAG_PHASE.setTranslationKey("ac_imag_phase");
        IMAG_PHASE.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(IMAG_PHASE);
        IMAGSIL_ORE.setRegistryName("academy:imagsil_ore");
        IMAGSIL_ORE.setTranslationKey("ac_imagsil_ore");
        IMAGSIL_ORE.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(IMAGSIL_ORE);
        MACHINE_FRAME.setRegistryName("academy:machine_frame");
        MACHINE_FRAME.setTranslationKey("ac_machine_frame");
        MACHINE_FRAME.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(MACHINE_FRAME);
        METAL_FORMER.setRegistryName("academy:metal_former");
        METAL_FORMER.setTranslationKey("ac_metal_former");
        METAL_FORMER.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(METAL_FORMER);
        NODE_ADVANCED.setRegistryName("academy:node_advanced");
        NODE_ADVANCED.setTranslationKey("ac_node_advanced");
        NODE_ADVANCED.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(NODE_ADVANCED);
        NODE_BASIC.setRegistryName("academy:node_basic");
        NODE_BASIC.setTranslationKey("ac_node_basic");
        NODE_BASIC.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(NODE_BASIC);
        NODE_STANDARD.setRegistryName("academy:node_standard");
        NODE_STANDARD.setTranslationKey("ac_node_standard");
        NODE_STANDARD.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(NODE_STANDARD);
        PHASE_GEN.setRegistryName("academy:phase_gen");
        PHASE_GEN.setTranslationKey("ac_phase_gen");
        PHASE_GEN.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(PHASE_GEN);
        RESO_ORE.setRegistryName("academy:reso_ore");
        RESO_ORE.setTranslationKey("ac_reso_ore");
        RESO_ORE.setCreativeTab(cn.academy.AcademyCraft.cct);
        RESO_ORE.setDropData(AcademyCraftItemList.RESO_CRYSTAL, 1, 2);
        BLOCK_LIST.add(RESO_ORE);
        SOLAR_GEN.setRegistryName("academy:solar_gen");
        SOLAR_GEN.setTranslationKey("ac_solar_gen");
        SOLAR_GEN.setCreativeTab(cn.academy.AcademyCraft.cct);
        BLOCK_LIST.add(SOLAR_GEN);
    }

    private AcademyCraftBlockList() {
    }
}
