package cn.academy.internal.tileentity;

import net.minecraft.tileentity.TileEntity;

public class TileImagPhase extends TileEntity {
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}