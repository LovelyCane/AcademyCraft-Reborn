package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.context.RegClientContext;
import cn.academy.entity.EntityMineRayLuck;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegClientContext(LuckMRContext.class)
@SideOnly(Side.CLIENT)
public class LuckMRContextC extends MRContextC {
    public LuckMRContextC(LuckMRContext par) {
        super(par);
    }

    @Override
    public Entity createRay() {
        return new EntityMineRayLuck(player);
    }
}
