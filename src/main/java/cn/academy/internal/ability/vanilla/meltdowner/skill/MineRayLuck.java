package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD, KSkun
 */
public class MineRayLuck extends MineRaysBase {
    public static final MineRayLuck INSTANCE = new MineRayLuck();

    public MineRayLuck() {
        super("luck", 5);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, LuckMRContext::new);
    }
}
