package cn.academy.internal.advancements.triggers;

import cn.academy.api.ability.Category;

/**
 * @author EAirPeter
 */
public class AchAbility<Cat extends Category> extends ACLevelTrigger
{

    //Ach
    //AchEv
    protected final Cat category;
    
    public AchAbility(Cat cat, String id, int level) {
        super(cat.getName() + "." + id, level);
        category = cat;
    }
}