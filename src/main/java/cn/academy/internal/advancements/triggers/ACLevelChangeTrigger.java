package cn.academy.internal.advancements.triggers;

import cn.academy.api.ability.Category;

/**
 * @author EAirPeter
 */
public final class ACLevelChangeTrigger<Cat extends Category> extends AchAbility<Cat>
{
    public ACLevelChangeTrigger(int lv, Cat cat, String id) {
        super(cat, id, lv);
    }
}