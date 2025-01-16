package cn.academy;

import cn.academy.internal.client.renderer.block.RenderCatEngine;
import cn.academy.internal.client.renderer.block.RenderPhaseGen;
import cn.academy.internal.tileentity.TileCatEngine;
import cn.academy.internal.tileentity.TilePhaseGen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked","rawtypes"})
public class AcademyCraftTileEntityRendererList {
    public static final List<TileEntityRenderer> TILE_ENTITY_RENDERER_MAP = new ArrayList<>();
    public static final RenderCatEngine RENDER_CATENGINE = new RenderCatEngine();
  //  public static final RenderImagPhaseLiquid RENDER_IMAG_PHASE_LIQUID = new RenderImagPhaseLiquid();
    public static final RenderPhaseGen RENDER_PHASE_GEN = new RenderPhaseGen();

    static {
        TILE_ENTITY_RENDERER_MAP.add(new TileEntityRenderer<>(TileCatEngine.class, RENDER_CATENGINE));
    //    TILE_ENTITY_RENDERER_MAP.add(new TileEntityRenderer<>(TileImagPhase.class, RENDER_IMAG_PHASE_LIQUID));
        TILE_ENTITY_RENDERER_MAP.add(new TileEntityRenderer<>(TilePhaseGen.class, RENDER_PHASE_GEN));
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
