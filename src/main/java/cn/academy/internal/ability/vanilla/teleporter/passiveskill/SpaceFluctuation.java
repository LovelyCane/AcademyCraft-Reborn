package cn.academy.internal.ability.vanilla.teleporter.passiveskill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.vanilla.teleporter.util.TPSkillHelper;

/**
 * Dummy placeholder. Impl at {@link TPSkillHelper}
 *
 * @author WeAthFolD
 */
public class SpaceFluctuation extends Skill {
    public static final SpaceFluctuation instance = new SpaceFluctuation();

    public SpaceFluctuation() {
        super("space_fluct", 4);
        this.canControl = false;
    }
}
