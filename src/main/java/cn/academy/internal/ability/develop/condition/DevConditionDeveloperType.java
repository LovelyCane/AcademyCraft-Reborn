package cn.academy.internal.ability.develop.condition;

import cn.academy.api.ability.Skill;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.ability.AbilityLocalization;
import cn.academy.internal.ability.develop.DeveloperType;
import cn.academy.internal.ability.develop.IDeveloper;
import net.minecraft.util.ResourceLocation;

/**
 * @author WeAthFolD
 */
public class DevConditionDeveloperType implements IDevCondition {
    
    final DeveloperType type;
    
    public DevConditionDeveloperType(DeveloperType _type) {
        type = _type;
    }

    @Override
    public boolean accepts(AbilityData data, IDeveloper developer, Skill skill) {
        return developer.getType().ordinal() >= type.ordinal();
    }
    
    @Override
    public ResourceLocation getIcon() {
        return type.texture;
    }

    @Override
    public String getHintText() {
        return AbilityLocalization.instance.machineType(type);
    }

}