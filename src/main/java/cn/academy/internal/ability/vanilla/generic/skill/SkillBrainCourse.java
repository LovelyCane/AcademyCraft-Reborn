package cn.academy.internal.ability.vanilla.generic.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.event.ability.CalcEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Generic skill: Brain Course.
 *
 * @author WeAthFolD
 */

public class SkillBrainCourse extends Skill {
    public SkillBrainCourse() {
        super("brain_course", 3);
        this.canControl = false;
        this.isGeneric = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void recalculateMaxCP(CalcEvent.MaxCP event) {
        AbilityData abilityData = AbilityData.get(event.player);
        if (abilityData.isSkillLearned(this)) {
            event.value += 1000;
        }
    }
}