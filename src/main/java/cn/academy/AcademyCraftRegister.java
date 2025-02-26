package cn.academy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AcademyCraftRegister {
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        for (Item item : AcademyCraftItemList.ITEM_LIST) {
            GameData.register_impl(item);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderer();
        }
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        for (Block block : AcademyCraftBlockList.BLOCK_LIST) {
            event.getRegistry().register(block);
        }
        for (Class<? extends TileEntity> tileEntityClass : AcademyCraftTileEntityList.TILE_ENTITY_LIST) {
            String name = tileEntityClass.getSimpleName().replace("TileEntity", "").replaceAll("([a-z])([A-Z])", "$1_$2").replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").toLowerCase();
            GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(Tags.MOD_ID, name));
        }
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        GameRegistry.addSmelting(AcademyCraftBlockList.CONSTRAINT_METAL, new ItemStack(AcademyCraftItemList.CONSTRAINT_INGOT), 0.7F);
        GameRegistry.addSmelting(AcademyCraftBlockList.IMAGSIL_ORE, new ItemStack(AcademyCraftItemList.IMAG_SILICON_INGOT), 0.7F);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderer() {
        for (Item item : AcademyCraftItemList.ITEM_LIST) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    public static void registerAllDuringInit() {
        registerEntity();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerTileEntityRender();
        }
    }

    public static void registerAllDuringPreInit() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            registerEntityRenderer();
        }
    }

    private static void registerEntity() {
        int num = 0;
        for (Class<? extends Entity> entity : AcademyCraftEntityList.ENTITY_LIST) {
            String name = entity.getSimpleName().replace("Entity", "").replaceAll("([a-z])([A-Z])", "$1_$2").replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").toLowerCase();
            EntityRegistry.registerModEntity(new ResourceLocation(Tags.MOD_ID, name), entity, entity.getSimpleName().toLowerCase(), num, AcademyCraft.instance, 32, 3, true);
            num++;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    private static void registerEntityRenderer() {
        for (Class<? extends Render> render : AcademyCraftEntityRenderList.ENTITY_RENDER_MAP.keySet()) {
            try {
                Constructor<?> constructor = render.getConstructor(RenderManager.class);
                Class<? extends Entity> entityClass = AcademyCraftEntityRenderList.ENTITY_RENDER_MAP.get(render);

                RenderingRegistry.registerEntityRenderingHandler(entityClass, (IRenderFactory<Entity>) manager -> {
                    try {
                        return (Render<? super Entity>) constructor.newInstance(manager);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Render class does not have required constructor: " + render, e);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    private static void registerTileEntityRender() {
        for (AcademyCraftTileEntityRendererList.TileEntityRenderer tileEntityRenderer : AcademyCraftTileEntityRendererList.TILE_ENTITY_RENDERER_LIST) {
            ClientRegistry.bindTileEntitySpecialRenderer(tileEntityRenderer.tileEntityClass, tileEntityRenderer.specialRenderer);
        }
    }

    private AcademyCraftRegister() {
    }
}
