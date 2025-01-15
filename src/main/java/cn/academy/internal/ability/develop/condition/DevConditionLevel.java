package cn.academy.internal.ability.develop.condition;

import cn.academy.api.ability.Skill;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.ability.develop.IDeveloper;
import net.minecraft.util.ResourceLocation;

/**
 * @author WeAthFolD
 */
public class DevConditionLevel implements IDevCondition {

    @Override
    public boolean accepts(AbilityData data, IDeveloper developer, Skill skill) {
        return data.getLevel() >= skill.getLevel();
    }
    
    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public String getHintText() {
        return null;
    }

    @Override
    public boolean shouldDisplay() {
        return false;
    }

}