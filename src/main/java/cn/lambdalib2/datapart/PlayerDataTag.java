/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.datapart;

import cn.lambdalib2.s11n.nbt.NBTS11n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A data tag for storage-only, lightweight stuffs
 * @author WeAthFolD
 */
@RegDataPart(EntityPlayer.class)
public class PlayerDataTag extends DataPart<EntityPlayer> {

    public static PlayerDataTag get(EntityPlayer player) {
        return EntityData.get(player).getPart(PlayerDataTag.class);
    }

    private NBTTagCompound theTag = new NBTTagCompound();

    {
        setNBTStorage();
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        theTag = tag;
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        NBTS11n.write(tag, theTag);
    }

    public NBTTagCompound getTag() {
        if(theTag == null)
            theTag = new NBTTagCompound();
        return theTag;
    }

}