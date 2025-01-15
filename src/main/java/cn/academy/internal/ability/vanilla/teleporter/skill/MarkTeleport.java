package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MarkTeleport extends Skill {
    public static final String SKILL_NAME = "mark_teleport";
    public static final int SKILL_LEVEL = 2;

    public static final MarkTeleport INSTANCE = new MarkTeleport();

    private MarkTeleport() {
        super(SKILL_NAME, SKILL_LEVEL);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, MTContext::new);
    }
}
