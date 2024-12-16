package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD, KSkun
 */

public class MineRayLuck extends MineRaysBase {
    public MineRayLuck() {
        super("luck", 5);
    }

    public static MineRayLuck instance = new MineRayLuck();

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey2(rt, keyID, LuckMRContext::new);
    }
}
