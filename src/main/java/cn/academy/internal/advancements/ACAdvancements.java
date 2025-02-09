package cn.academy.internal.advancements;

import cn.academy.AcademyCraft;
import cn.academy.Resources;
import cn.academy.internal.advancements.triggers.ACTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

/**
 * Automatically generated by LambdaLib2.xconf in 2019-02-06 21:37:03.
 */
public class ACAdvancements {
    public static final ACTrigger ac_developer = new ACTrigger("ac_developer");

    public static final ACTrigger ac_exp_full = new ACTrigger("ac_exp_full");

    public static final ACTrigger ac_learning_skill = new ACTrigger("ac_learning_skill");

    public static final ACTrigger ac_level_3 = new ACTrigger("ac_level_3");

    public static final ACTrigger ac_level_5 = new ACTrigger("ac_level_5");

    public static final ACTrigger ac_node = new ACTrigger("ac_node");

    public static final ACTrigger ac_overload = new ACTrigger("ac_overload");

    public static final ACTrigger convert_category = new ACTrigger("convert_category");

    public static final ACTrigger dev_category = new ACTrigger("dev_category");

    public static final ACTrigger getting_phase = new ACTrigger("getting_phase");

    public static final ACTrigger phase_generator = new ACTrigger("phase_generator");

    public static final ACTrigger terminal_installed = new ACTrigger("terminal_installed");

    public static void init() {
        DispatcherAch.init();
        CriteriaTriggers.register(ac_developer);
        CriteriaTriggers.register(ac_exp_full);
        CriteriaTriggers.register(ac_learning_skill);
        CriteriaTriggers.register(ac_level_3);
        CriteriaTriggers.register(ac_level_5);
        CriteriaTriggers.register(ac_node);
        CriteriaTriggers.register(ac_overload);
        CriteriaTriggers.register(convert_category);
        CriteriaTriggers.register(dev_category);
        CriteriaTriggers.register(getting_phase);
        CriteriaTriggers.register(phase_generator);
        CriteriaTriggers.register(terminal_installed);
    }

    /**
     * Trigger an achievement
     *
     * @param player The player
     * @param achid  The id of the achievement
     * @return true if succeeded
     * This method is server-only. --Paindar
     */
    public static boolean trigger(EntityPlayer player, ResourceLocation achid) {
        if (!(player instanceof EntityPlayerMP))
            return false;

        ICriterionTrigger ach = CriteriaTriggers.get(achid);
        if ((!(ach instanceof ACTrigger))) {
            AcademyCraft.LOGGER.warn("AC Achievement '{}' does not exist", achid);
            return false;
        }
        ((ACTrigger) ach).trigger((EntityPlayerMP) player);
        return true;
    }

    public static boolean trigger(EntityPlayer player, String achid) {
        return trigger(player, Resources.res(achid));
    }
}