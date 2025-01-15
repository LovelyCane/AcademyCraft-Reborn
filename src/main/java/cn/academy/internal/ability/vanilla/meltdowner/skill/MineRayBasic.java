package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD, KSkun
 */

public class MineRayBasic extends MineRaysBase {
    public static final MineRayBasic INSTANCE = new MineRayBasic();

    public MineRayBasic() {
        super("basic", 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, BasicMRContext::new);
    }
}