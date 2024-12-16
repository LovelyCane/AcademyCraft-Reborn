package cn.academy.ability.vanilla.meltdowner.passiveskill;

import cn.academy.ability.Skill;
import cn.academy.datapart.AbilityData;
import cn.academy.datapart.CPData;
import cn.lambdalib2.util.MathUtils;

public class RadiationIntensify extends Skill {
    public static final RadiationIntensify INSTANCE = new RadiationIntensify();

    private RadiationIntensify() {
        super("rad_intensify", 1);
        canControl = false;
        expCustomized = true;
    }

    @Override
    public float getSkillExp(AbilityData data) {
        CPData cpData = CPData.get(data.getEntity());
        return MathUtils.clampf(0, 1, cpData.getMaxCP() / CPData.get(data.getEntity()).getInitCP(5));
    }

    public float getRate(AbilityData data) {
        return MathUtils.lerpf(1.4f, 1.8f, data.getSkillExp(this));
    }
}
