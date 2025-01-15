package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityMineRayBasic;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegClientContext(BasicMRContext.class)
public class BasicMRContextC extends MRContextC {
    public BasicMRContextC(BasicMRContext par) {
        super(par);
    }

    @Override
    public Entity createRay() {
        return new EntityMineRayBasic(player);
    }
}