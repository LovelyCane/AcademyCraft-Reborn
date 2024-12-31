package cn.academy;

import cn.lambdalib2.registry.RegistryCallback;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ACItems {
    public static final cn.academy.item.ItemApp app_freq_transmitter = new cn.academy.item.ItemApp("freq_transmitter");
    public static final cn.academy.item.ItemApp app_media_player = new cn.academy.item.ItemApp("media_player");
    public static final cn.academy.item.ItemApp app_skill_tree = new cn.academy.item.ItemApp("skill_tree");
    public static final net.minecraft.item.Item brain_component = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item calc_chip = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemCoin coin = new cn.academy.item.ItemCoin();
    public static final net.minecraft.item.Item constraint_ingot = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item constraint_plate = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item crystal_low = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item crystal_normal = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item crystal_pure = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item data_chip = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemDeveloper developer_portable = new cn.academy.item.ItemDeveloper();
    public static final net.minecraft.item.Item energy_convert_component = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemEnergyBase energy_unit = new cn.academy.item.ItemEnergyBase(10000, 20);
    public static final net.minecraft.item.Item imag_silicon_ingot = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item imag_silicon_piece = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemInductionFactor induction_factor = new cn.academy.item.ItemInductionFactor();
    public static final net.minecraft.item.Item info_component = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item logo = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemMagHook mag_hook = new cn.academy.item.ItemMagHook();
    public static final cn.academy.item.ItemMagneticCoil magnetic_coil = new cn.academy.item.ItemMagneticCoil();
    public static final cn.academy.item.ItemMatrixCore mat_core = new cn.academy.item.ItemMatrixCore();
    public static final cn.academy.item.ItemMatterUnit matter_unit = new cn.academy.item.ItemMatterUnit();
    public static final net.minecraft.item.Item needle = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item reinforced_iron_plate = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item reso_crystal = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item resonance_component = new net.minecraft.item.Item();
    public static final cn.academy.item.ItemSilbarn silbarn = new cn.academy.item.ItemSilbarn();
    public static final cn.academy.item.ItemTerminalInstaller terminal_installer = new cn.academy.item.ItemTerminalInstaller();
    public static final net.minecraft.item.Item wafer = new net.minecraft.item.Item();
    public static final net.minecraft.item.Item windgen_fan = new net.minecraft.item.Item();

    @RegistryCallback
    @SuppressWarnings("sideonly")
    private static void registerItems(RegistryEvent.Register<Item> event) {
        app_freq_transmitter.setRegistryName("academy:app_freq_transmitter");
        app_freq_transmitter.setTranslationKey("ac_apps");
        app_freq_transmitter.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(app_freq_transmitter);
        app_media_player.setRegistryName("academy:app_media_player");
        app_media_player.setTranslationKey("ac_apps");
        app_media_player.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(app_media_player);
        app_skill_tree.setRegistryName("academy:app_skill_tree");
        app_skill_tree.setTranslationKey("ac_apps");
        app_skill_tree.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(app_skill_tree);
        brain_component.setRegistryName("academy:brain_component");
        brain_component.setTranslationKey("ac_brain_component");
        brain_component.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(brain_component);
        calc_chip.setRegistryName("academy:calc_chip");
        calc_chip.setTranslationKey("ac_calc_chip");
        calc_chip.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(calc_chip);
        coin.setRegistryName("academy:coin");
        coin.setTranslationKey("ac_coin");
        coin.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(coin);
        coin.afterRegistry();
        constraint_ingot.setRegistryName("academy:constraint_ingot");
        constraint_ingot.setTranslationKey("ac_constraint_ingot");
        constraint_ingot.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(constraint_ingot);
        constraint_plate.setRegistryName("academy:constraint_plate");
        constraint_plate.setTranslationKey("ac_constraint_plate");
        constraint_plate.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(constraint_plate);
        crystal_low.setRegistryName("academy:crystal_low");
        crystal_low.setTranslationKey("ac_crystal_low");
        crystal_low.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(crystal_low);
        crystal_normal.setRegistryName("academy:crystal_normal");
        crystal_normal.setTranslationKey("ac_crystal_normal");
        crystal_normal.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(crystal_normal);
        crystal_pure.setRegistryName("academy:crystal_pure");
        crystal_pure.setTranslationKey("ac_crystal_pure");
        crystal_pure.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(crystal_pure);
        data_chip.setRegistryName("academy:data_chip");
        data_chip.setTranslationKey("ac_data_chip");
        data_chip.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(data_chip);
        developer_portable.setRegistryName("academy:developer_portable");
        developer_portable.setTranslationKey("ac_developer_portable");
        developer_portable.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(developer_portable);
        developer_portable.afterRegistry();
        energy_convert_component.setRegistryName("academy:energy_convert_component");
        energy_convert_component.setTranslationKey("ac_energy_convert_component");
        energy_convert_component.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(energy_convert_component);
        energy_unit.setRegistryName("academy:energy_unit");
        energy_unit.setTranslationKey("ac_energy_unit");
        energy_unit.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(energy_unit);
        imag_silicon_ingot.setRegistryName("academy:imag_silicon_ingot");
        imag_silicon_ingot.setTranslationKey("ac_imag_silicon_ingot");
        imag_silicon_ingot.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(imag_silicon_ingot);
        imag_silicon_piece.setRegistryName("academy:imag_silicon_piece");
        imag_silicon_piece.setTranslationKey("ac_imag_silicon_piece");
        imag_silicon_piece.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(imag_silicon_piece);
        induction_factor.setRegistryName("academy:induction_factor");
        induction_factor.setTranslationKey("ac_induction_factor");
        induction_factor.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(induction_factor);
        info_component.setRegistryName("academy:info_component");
        info_component.setTranslationKey("ac_info_component");
        info_component.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(info_component);
        logo.setRegistryName("academy:logo");
        logo.setTranslationKey("ac_logo");
        event.getRegistry().register(logo);
        mag_hook.setRegistryName("academy:mag_hook");
        mag_hook.setTranslationKey("ac_mag_hook");
        mag_hook.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(mag_hook);
        mag_hook.afterRegistry();
        magnetic_coil.setRegistryName("academy:magnetic_coil");
        magnetic_coil.setTranslationKey("ac_magnetic_coil");
        magnetic_coil.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(magnetic_coil);
        mat_core.setRegistryName("academy:mat_core");
        mat_core.setTranslationKey("ac_mat_core");
        mat_core.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(mat_core);
        matter_unit.setRegistryName("academy:matter_unit");
        matter_unit.setTranslationKey("ac_matter_unit");
        matter_unit.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(matter_unit);
        needle.setRegistryName("academy:needle");
        needle.setTranslationKey("ac_needle");
        needle.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(needle);
        reinforced_iron_plate.setRegistryName("academy:reinforced_iron_plate");
        reinforced_iron_plate.setTranslationKey("ac_reinforced_iron_plate");
        reinforced_iron_plate.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(reinforced_iron_plate);
        reso_crystal.setRegistryName("academy:reso_crystal");
        reso_crystal.setTranslationKey("ac_reso_crystal");
        reso_crystal.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(reso_crystal);
        resonance_component.setRegistryName("academy:resonance_component");
        resonance_component.setTranslationKey("ac_resonance_component");
        resonance_component.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(resonance_component);
        silbarn.setRegistryName("academy:silbarn");
        silbarn.setTranslationKey("ac_silbarn");
        silbarn.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(silbarn);
        silbarn.afterRegistry();
        terminal_installer.setRegistryName("academy:terminal_installer");
        terminal_installer.setTranslationKey("ac_terminal_installer");
        terminal_installer.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(terminal_installer);
        wafer.setRegistryName("academy:wafer");
        wafer.setTranslationKey("ac_wafer");
        wafer.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(wafer);
        windgen_fan.setRegistryName("academy:windgen_fan");
        windgen_fan.setTranslationKey("ac_windgen_fan");
        windgen_fan.setCreativeTab(cn.academy.AcademyCraft.cct);
        windgen_fan.setMaxStackSize(1);
        windgen_fan.setMaxDamage(100);
        event.getRegistry().register(windgen_fan);

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderers() {
        ModelLoader.setCustomModelResourceLocation(app_freq_transmitter, 0, new ModelResourceLocation("academy:app_freq_transmitter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(app_media_player, 0, new ModelResourceLocation("academy:app_media_player", "inventory"));
        ModelLoader.setCustomModelResourceLocation(app_skill_tree, 0, new ModelResourceLocation("academy:app_skill_tree", "inventory"));
        ModelLoader.setCustomModelResourceLocation(brain_component, 0, new ModelResourceLocation("academy:brain_component", "inventory"));
        ModelLoader.setCustomModelResourceLocation(calc_chip, 0, new ModelResourceLocation("academy:calc_chip", "inventory"));
        ModelLoader.setCustomModelResourceLocation(constraint_ingot, 0, new ModelResourceLocation("academy:constraint_ingot", "inventory"));
        ModelLoader.setCustomModelResourceLocation(constraint_plate, 0, new ModelResourceLocation("academy:constraint_plate", "inventory"));
        ModelLoader.setCustomModelResourceLocation(crystal_low, 0, new ModelResourceLocation("academy:crystal_low", "inventory"));
        ModelLoader.setCustomModelResourceLocation(crystal_normal, 0, new ModelResourceLocation("academy:crystal_normal", "inventory"));
        ModelLoader.setCustomModelResourceLocation(crystal_pure, 0, new ModelResourceLocation("academy:crystal_pure", "inventory"));
        ModelLoader.setCustomModelResourceLocation(data_chip, 0, new ModelResourceLocation("academy:data_chip", "inventory"));
        ModelLoader.setCustomModelResourceLocation(energy_convert_component, 0, new ModelResourceLocation("academy:energy_convert_component", "inventory"));
        ModelLoader.setCustomModelResourceLocation(energy_unit, 0, new ModelResourceLocation("academy:energy_unit", "inventory"));
        ModelLoader.setCustomModelResourceLocation(imag_silicon_ingot, 0, new ModelResourceLocation("academy:imag_silicon_ingot", "inventory"));
        ModelLoader.setCustomModelResourceLocation(imag_silicon_piece, 0, new ModelResourceLocation("academy:imag_silicon_piece", "inventory"));
        ModelLoader.setCustomModelResourceLocation(induction_factor, 2, new ModelResourceLocation("academy:factor_teleporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(induction_factor, 0, new ModelResourceLocation("academy:factor_electromaster", "inventory"));
        ModelLoader.setCustomModelResourceLocation(induction_factor, 3, new ModelResourceLocation("academy:factor_vecmanip", "inventory"));
        ModelLoader.setCustomModelResourceLocation(induction_factor, 1, new ModelResourceLocation("academy:factor_meltdowner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(info_component, 0, new ModelResourceLocation("academy:info_component", "inventory"));
        ModelLoader.setCustomModelResourceLocation(logo, 0, new ModelResourceLocation("academy:logo", "inventory"));
        ModelLoader.setCustomModelResourceLocation(magnetic_coil, 0, new ModelResourceLocation("academy:magnetic_coil", "inventory"));
        ModelLoader.setCustomModelResourceLocation(mat_core, 0, new ModelResourceLocation("academy:mat_core_0", "inventory"));
        ModelLoader.setCustomModelResourceLocation(mat_core, 2, new ModelResourceLocation("academy:mat_core_2", "inventory"));
        ModelLoader.setCustomModelResourceLocation(mat_core, 1, new ModelResourceLocation("academy:mat_core_1", "inventory"));
        ModelLoader.setCustomModelResourceLocation(matter_unit, 1, new ModelResourceLocation("academy:matter_unit_phase_liquid_0", "inventory"));
        ModelLoader.setCustomModelResourceLocation(matter_unit, 0, new ModelResourceLocation("academy:matter_unit", "inventory"));
        ModelLoader.setCustomModelResourceLocation(needle, 0, new ModelResourceLocation("academy:needle", "inventory"));
        ModelLoader.setCustomModelResourceLocation(reinforced_iron_plate, 0, new ModelResourceLocation("academy:reinforced_iron_plate", "inventory"));
        ModelLoader.setCustomModelResourceLocation(reso_crystal, 0, new ModelResourceLocation("academy:reso_crystal", "inventory"));
        ModelLoader.setCustomModelResourceLocation(resonance_component, 0, new ModelResourceLocation("academy:resonance_component", "inventory"));
        ModelLoader.setCustomModelResourceLocation(terminal_installer, 0, new ModelResourceLocation("academy:terminal_installer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(wafer, 0, new ModelResourceLocation("academy:wafer", "inventory"));
        ModelLoader.setCustomModelResourceLocation(windgen_fan, 0, new ModelResourceLocation("academy:windgen_fan", "inventory"));
    }
}