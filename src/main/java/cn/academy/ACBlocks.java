package cn.academy;

import cn.academy.ability.develop.DeveloperType;
import cn.academy.block.block.*;
import cn.academy.block.block.BlockNode.NodeType;
import cn.lambdalib2.multiblock.ItemBlockMulti;
import cn.lambdalib2.registry.RegistryCallback;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ACBlocks {
    public static final BlockCatEngine cat_engine = new BlockCatEngine();
    public static final BlockGenericOre constraint_metal = new BlockGenericOre(4.0f, 1);
    public static final BlockGenericOre crystal_ore = new BlockGenericOre(3.0f, 2);
    public static final BlockDeveloper dev_advanced = new BlockDeveloper(DeveloperType.ADVANCED);
    public static final BlockDeveloper dev_normal = new BlockDeveloper(DeveloperType.NORMAL);
    public static final BlockImagFusor imag_fusor = new BlockImagFusor();
    public static final BlockImagPhase imag_phase = new BlockImagPhase();
    public static final BlockGenericOre imagsil_ore = new BlockGenericOre(3.75f, 2);
    public static final net.minecraft.block.Block machine_frame = new net.minecraft.block.Block(Material.ROCK);
    public static final BlockMetalFormer metal_former = new BlockMetalFormer();
    public static final BlockNode node_advanced = new BlockNode(NodeType.ADVANCED);
    public static final BlockNode node_basic = new BlockNode(NodeType.BASIC);
    public static final BlockNode node_standard = new BlockNode(NodeType.STANDARD);
    public static final BlockPhaseGen phase_gen = new BlockPhaseGen();
    public static final BlockGenericOre reso_ore = new BlockGenericOre(3f, 2);
    public static final BlockSolarGen solar_gen = new BlockSolarGen();
    public static final BlockWindGenBase windgen_base = new BlockWindGenBase();
    public static final BlockWindGenMain windgen_main = new BlockWindGenMain();
    public static final BlockWindGenPillar windgen_pillar = new BlockWindGenPillar();

    public static final ItemBlock item_cat_engine = new ItemBlock(cat_engine);
    public static final ItemBlock item_constraint_metal = new ItemBlock(constraint_metal);
    public static final ItemBlock item_crystal_ore = new ItemBlock(crystal_ore);
    public static final ItemBlockMulti item_dev_advanced = new ItemBlockMulti(dev_advanced);
    public static final ItemBlockMulti item_dev_normal = new ItemBlockMulti(dev_normal);
    public static final ItemBlock item_imag_fusor = new ItemBlock(imag_fusor);
    public static final ItemBlock item_imag_phase = new ItemBlock(imag_phase);
    public static final ItemBlock item_imagsil_ore = new ItemBlock(imagsil_ore);
    public static final ItemBlock item_machine_frame = new ItemBlock(machine_frame);
    public static final ItemBlock item_metal_former = new ItemBlock(metal_former);
    public static final ItemBlock item_node_advanced = new ItemBlock(node_advanced);
    public static final ItemBlock item_node_basic = new ItemBlock(node_basic);
    public static final ItemBlock item_node_standard = new ItemBlock(node_standard);
    public static final ItemBlock item_phase_gen = new ItemBlock(phase_gen);
    public static final ItemBlock item_reso_ore = new ItemBlock(reso_ore);
    public static final ItemBlock item_solar_gen = new ItemBlock(solar_gen);
    public static final ItemBlock item_windgen_base = new ItemBlock(windgen_base);
    public static final ItemBlock item_windgen_main = new ItemBlock(windgen_main);
    public static final ItemBlock item_windgen_pillar = new ItemBlock(windgen_pillar);

    @RegistryCallback
    @SuppressWarnings("unused")
    private static void registerBlocks(RegistryEvent.Register<Block> event) {
        cat_engine.setRegistryName("academy:cat_engine");
        cat_engine.setTranslationKey("ac_cat_engine");
        cat_engine.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(cat_engine);
        constraint_metal.setRegistryName("academy:constraint_metal");
        constraint_metal.setTranslationKey("ac_constraint_metal");
        constraint_metal.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(constraint_metal);
        crystal_ore.setRegistryName("academy:crystal_ore");
        crystal_ore.setTranslationKey("ac_crystal_ore");
        crystal_ore.setCreativeTab(cn.academy.AcademyCraft.cct);
        crystal_ore.setDropData(ACItems.crystal_low, 1, 3);
        event.getRegistry().register(crystal_ore);
        dev_advanced.setRegistryName("academy:dev_advanced");
        dev_advanced.setTranslationKey("ac_dev_advanced");
        dev_advanced.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(dev_advanced);
        dev_normal.setRegistryName("academy:dev_normal");
        dev_normal.setTranslationKey("ac_dev_normal");
        dev_normal.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(dev_normal);
        imag_fusor.setRegistryName("academy:imag_fusor");
        imag_fusor.setTranslationKey("ac_imag_fusor");
        imag_fusor.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(imag_fusor);
        imag_phase.setRegistryName("academy:imag_phase");
        imag_phase.setTranslationKey("ac_imag_phase");
        imag_phase.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(imag_phase);
        imagsil_ore.setRegistryName("academy:imagsil_ore");
        imagsil_ore.setTranslationKey("ac_imagsil_ore");
        imagsil_ore.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(imagsil_ore);
        machine_frame.setRegistryName("academy:machine_frame");
        machine_frame.setTranslationKey("ac_machine_frame");
        machine_frame.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(machine_frame);
        metal_former.setRegistryName("academy:metal_former");
        metal_former.setTranslationKey("ac_metal_former");
        metal_former.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(metal_former);
        node_advanced.setRegistryName("academy:node_advanced");
        node_advanced.setTranslationKey("ac_node_advanced");
        node_advanced.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(node_advanced);
        node_basic.setRegistryName("academy:node_basic");
        node_basic.setTranslationKey("ac_node_basic");
        node_basic.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(node_basic);
        node_standard.setRegistryName("academy:node_standard");
        node_standard.setTranslationKey("ac_node_standard");
        node_standard.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(node_standard);
        phase_gen.setRegistryName("academy:phase_gen");
        phase_gen.setTranslationKey("ac_phase_gen");
        phase_gen.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(phase_gen);
        reso_ore.setRegistryName("academy:reso_ore");
        reso_ore.setTranslationKey("ac_reso_ore");
        reso_ore.setCreativeTab(cn.academy.AcademyCraft.cct);
        reso_ore.setDropData(ACItems.reso_crystal, 1, 2);
        event.getRegistry().register(reso_ore);
        solar_gen.setRegistryName("academy:solar_gen");
        solar_gen.setTranslationKey("ac_solar_gen");
        solar_gen.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(solar_gen);
        windgen_base.setRegistryName("academy:windgen_base");
        windgen_base.setTranslationKey("ac_windgen_base");
        windgen_base.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(windgen_base);
        windgen_main.setRegistryName("academy:windgen_main");
        windgen_main.setTranslationKey("ac_windgen_main");
        windgen_main.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(windgen_main);
        windgen_pillar.setRegistryName("academy:windgen_pillar");
        windgen_pillar.setTranslationKey("ac_windgen_pillar");
        windgen_pillar.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(windgen_pillar);
    }

    @RegistryCallback
    @SuppressWarnings({"sideonly", "unused"})
    private static void registerItems(RegistryEvent.Register<Item> event) {
        item_cat_engine.setRegistryName("academy:cat_engine");
        item_cat_engine.setTranslationKey("ac_cat_engine");
        event.getRegistry().register(item_cat_engine);
        item_constraint_metal.setRegistryName("academy:constraint_metal");
        item_constraint_metal.setTranslationKey("ac_constraint_metal");
        event.getRegistry().register(item_constraint_metal);
        item_crystal_ore.setRegistryName("academy:crystal_ore");
        item_crystal_ore.setTranslationKey("ac_crystal_ore");
        event.getRegistry().register(item_crystal_ore);
        item_dev_advanced.setRegistryName("academy:dev_advanced");
        item_dev_advanced.setTranslationKey("ac_dev_advanced");
        event.getRegistry().register(item_dev_advanced);
        item_dev_normal.setRegistryName("academy:dev_normal");
        item_dev_normal.setTranslationKey("ac_dev_normal");
        event.getRegistry().register(item_dev_normal);
        item_imag_fusor.setRegistryName("academy:imag_fusor");
        item_imag_fusor.setTranslationKey("ac_imag_fusor");
        event.getRegistry().register(item_imag_fusor);
        item_imag_phase.setRegistryName("academy:imag_phase");
        item_imag_phase.setTranslationKey("ac_imag_phase");
        event.getRegistry().register(item_imag_phase);
        item_imagsil_ore.setRegistryName("academy:imagsil_ore");
        item_imagsil_ore.setTranslationKey("ac_imagsil_ore");
        event.getRegistry().register(item_imagsil_ore);
        item_machine_frame.setRegistryName("academy:machine_frame");
        item_machine_frame.setTranslationKey("ac_machine_frame");
        event.getRegistry().register(item_machine_frame);

        item_metal_former.setRegistryName("academy:metal_former");
        item_metal_former.setTranslationKey("ac_metal_former");
        event.getRegistry().register(item_metal_former);
        item_node_advanced.setRegistryName("academy:node_advanced");
        item_node_advanced.setTranslationKey("ac_node_advanced");
        event.getRegistry().register(item_node_advanced);
        item_node_basic.setRegistryName("academy:node_basic");
        item_node_basic.setTranslationKey("ac_node_basic");
        event.getRegistry().register(item_node_basic);
        item_node_standard.setRegistryName("academy:node_standard");
        item_node_standard.setTranslationKey("ac_node_standard");
        event.getRegistry().register(item_node_standard);
        item_phase_gen.setRegistryName("academy:phase_gen");
        item_phase_gen.setTranslationKey("ac_phase_gen");
        event.getRegistry().register(item_phase_gen);
        item_reso_ore.setRegistryName("academy:reso_ore");
        item_reso_ore.setTranslationKey("ac_reso_ore");
        event.getRegistry().register(item_reso_ore);
        item_solar_gen.setRegistryName("academy:solar_gen");
        item_solar_gen.setTranslationKey("ac_solar_gen");
        event.getRegistry().register(item_solar_gen);
        item_windgen_base.setRegistryName("academy:windgen_base");
        item_windgen_base.setTranslationKey("ac_windgen_base");
        event.getRegistry().register(item_windgen_base);
        item_windgen_main.setRegistryName("academy:windgen_main");
        item_windgen_main.setTranslationKey("ac_windgen_main");
        event.getRegistry().register(item_windgen_main);
        item_windgen_pillar.setRegistryName("academy:windgen_pillar");
        item_windgen_pillar.setTranslationKey("ac_windgen_pillar");
        event.getRegistry().register(item_windgen_pillar);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderers() {
        ModelLoader.setCustomModelResourceLocation(item_cat_engine, 0, new ModelResourceLocation(cat_engine.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_constraint_metal, 0, new ModelResourceLocation(constraint_metal.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_crystal_ore, 0, new ModelResourceLocation(crystal_ore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_dev_advanced, 0, new ModelResourceLocation(dev_advanced.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_dev_normal, 0, new ModelResourceLocation(dev_normal.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_imag_fusor, 0, new ModelResourceLocation(imag_fusor.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_imag_phase, 0, new ModelResourceLocation(imag_phase.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_imagsil_ore, 0, new ModelResourceLocation(imagsil_ore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_machine_frame, 0, new ModelResourceLocation(machine_frame.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_metal_former, 0, new ModelResourceLocation(metal_former.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_node_advanced, 0, new ModelResourceLocation(node_advanced.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_node_basic, 0, new ModelResourceLocation(node_basic.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_node_standard, 0, new ModelResourceLocation(node_standard.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_phase_gen, 0, new ModelResourceLocation(phase_gen.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_reso_ore, 0, new ModelResourceLocation(reso_ore.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_solar_gen, 0, new ModelResourceLocation(solar_gen.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_windgen_base, 0, new ModelResourceLocation(windgen_base.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_windgen_main, 0, new ModelResourceLocation(windgen_main.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(item_windgen_pillar, 0, new ModelResourceLocation(windgen_pillar.getRegistryName(), "inventory"));
    }
}