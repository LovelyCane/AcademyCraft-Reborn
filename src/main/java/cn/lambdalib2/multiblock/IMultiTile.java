package cn.lambdalib2.multiblock;

/**
 * Mark on any TileEntity that supports an BlockMulti. Provide the
 * InfoBlockMulti and handle the saving and loading (via loading from NBT) You
 * can visit TileMulti for impl reference.
 * 
 * @author WeathFolD
 */
public interface IMultiTile {

    InfoBlockMulti getBlockInfo();

    void setBlockInfo(InfoBlockMulti i);

}
