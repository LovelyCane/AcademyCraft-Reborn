package cn.academy;

import cn.academy.internal.tileentity.*;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class AcademyCraftTileEntityList {
    public static final List<Class<? extends TileEntity>> TILE_ENTITY_LIST = new ArrayList<>();

    static {
        TILE_ENTITY_LIST.add(TilePhaseGen.class);
        TILE_ENTITY_LIST.add(TileEntity.class);
        TILE_ENTITY_LIST.add(TileCatEngine.class);
        TILE_ENTITY_LIST.add(TileImagFusor.class);
        TILE_ENTITY_LIST.add(TileImagPhase.class);
        TILE_ENTITY_LIST.add(TileMetalFormer.class);
        TILE_ENTITY_LIST.add(TileNode.class);
        TILE_ENTITY_LIST.add(TileSolarGen.class);
    }

    private AcademyCraftTileEntityList() {
    }
}
