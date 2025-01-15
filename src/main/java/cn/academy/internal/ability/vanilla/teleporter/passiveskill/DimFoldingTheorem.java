package cn.academy.internal.ability.vanilla.teleporter.passiveskill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.vanilla.teleporter.util.TPSkillHelper;

/**
 * Dummy placeholder. Impl at {@link TPSkillHelper}
 *
 * @author WeAthFolD
 */
public class DimFoldingTheorem extends Skill {
    public static final DimFoldingTheorem instance = new DimFoldingTheorem();
    
    public DimFoldingTheorem()
    {
        super("dim_folding_theorem", 1);
        canControl = false;
    }
}
