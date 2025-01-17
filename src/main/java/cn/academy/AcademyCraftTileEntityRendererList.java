package cn.academy;

import cn.academy.internal.client.renderer.tileentity.TileCatEngineRenderer;
import cn.academy.internal.client.renderer.tileentity.TileImagPhaseRenderer;
import cn.academy.internal.client.renderer.tileentity.TilePhaseGenRenderer;
import cn.academy.internal.tileentity.TileCatEngine;
import cn.academy.internal.tileentity.TileImagPhase;
import cn.academy.internal.tileentity.TilePhaseGen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public class AcademyCraftTileEntityRendererList {
    public static final List<TileEntityRenderer> TILE_ENTITY_RENDERER_LIST = new ArrayList<>();
    public static final TileCatEngineRenderer RENDER_CATENGINE = new TileCatEngineRenderer();
    public static final TileImagPhaseRenderer RENDER_IMAG_PHASE_LIQUID = new TileImagPhaseRenderer();
    public static final TilePhaseGenRenderer RENDER_PHASE_GEN = new TilePhaseGenRenderer();

    static {
        TILE_ENTITY_RENDERER_LIST.add(new TileEntityRenderer<>(TileCatEngine.class, RENDER_CATENGINE));
        TILE_ENTITY_RENDERER_LIST.add(new TileEntityRenderer<>(TileImagPhase.class, RENDER_IMAG_PHASE_LIQUID));
        TILE_ENTITY_RENDERER_LIST.add(new TileEntityRenderer<>(TilePhaseGen.class, RENDER_PHASE_GEN));
    }

    public static class TileEntityRenderer<T extends TileEntity> {
        Class<T> tileEntityClass;
        TileEntitySpecialRenderer<? super T> specialRenderer;

        public TileEntityRenderer(Class<T> tileEntityClass, TileEntitySpecialRenderer<? super T> specialRenderer) {
            this.tileEntityClass = tileEntityClass;
            this.specialRenderer = specialRenderer;
        }
    }

    private AcademyCraftTileEntityRendererList() {
    }
}
