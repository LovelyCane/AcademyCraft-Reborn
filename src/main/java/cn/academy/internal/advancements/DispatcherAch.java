package cn.academy.internal.advancements;

import cn.academy.AcademyCraftItemList;
import cn.academy.api.ability.Category;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.vanilla.VanillaCategories;
import cn.academy.internal.advancements.triggers.ACTrigger;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.event.MatterUnitHarvestEvent;
import cn.academy.internal.event.ability.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author EAirPeter
 */
public final class DispatcherAch {

    public static final DispatcherAch INSTANCE = new DispatcherAch();
    
    
    //net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent
    
    private final HashMap<Item, HashSet<ACTrigger>> hcItemCrafted = new HashMap<>();
    
    public void rgItemCrafted(Item item, ACTrigger ach) {
        if (hcItemCrafted.containsKey(item))
            hcItemCrafted.get(item).add(ach);
        else {
            HashSet<ACTrigger> set = new HashSet<>();
            set.add(ach);
            hcItemCrafted.put(item, set);
        }
        hcItemCrafted.get(item).add(ach);
    }
    
    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        HashSet<ACTrigger> set = hcItemCrafted.get(event.crafting.getItem());
        if (set != null)
            if (event.player instanceof EntityPlayerMP)
                for (ACTrigger a : set)
                    ACAdvancements.trigger(event.player, a.getId());
    }
    
    
    //cn.academy.event.ability.LevelChangeEvent
    
    private final HashMap<Category, ACTrigger[]> hcLevelChange = new HashMap<>();

    public void rgLevelChange(int lv, ACTrigger ach) {
        rgLevelChange(VanillaCategories.electromaster, lv, ach);
        rgLevelChange(VanillaCategories.meltdowner, lv, ach);
        rgLevelChange(VanillaCategories.teleporter, lv, ach);
        rgLevelChange(VanillaCategories.vecManip, lv, ach);
    }

    public void rgLevelChange(Category cat, int lv, ACTrigger ach) {
        if (hcLevelChange.containsKey(cat))
            hcLevelChange.get(cat)[lv - 1] = ach;
        else {
            ACTrigger[] arr = new ACTrigger[5];
            arr[lv - 1] = ach;
            hcLevelChange.put(cat, arr);
        }
    }

    @SubscribeEvent
    public void onLevelChange(LevelChangeEvent event) {
        AbilityData data = AbilityData.get(event.player);
        if (data.hasCategory()) {
            int xlv = data.getLevel() - 1;
            ACTrigger[] arr = hcLevelChange.get(data.getCategory());
            if(event.player instanceof EntityPlayerMP){
                if (arr != null && xlv >= 0 && arr[xlv] != null)
                    ACAdvancements.trigger(event.player, arr[xlv].ID);
            }
        }
    }
    
    @SubscribeEvent
    public void onMatterUnitHarvest(MatterUnitHarvestEvent event) {
        if(event.player instanceof EntityPlayerMP)
            ACAdvancements.trigger(event.player, ACAdvancements.getting_phase.ID);
    }

    private final HashMap<Skill, ACTrigger> hcSkillLearn = new HashMap<>();

    @SubscribeEvent
    public void onSkillLearn(SkillLearnEvent event) {
        ACTrigger ach = hcSkillLearn.get(event.skill);//CHANGED HERE
        if (ach != null)
            if(event.player instanceof EntityPlayerMP)
                ach.trigger((EntityPlayerMP) event.player);
    }

    private final Map<Item, ACTrigger> hcPlayerPickup = new HashMap<>();
    
    public void rgPlayerPickup(ItemStack stack, ACTrigger ach) {
        hcPlayerPickup.put(stack.getItem(), ach);
    }
    
    @SubscribeEvent
    public void onPlayerPickup(PlayerEvent.ItemPickupEvent event) {
        ItemStack stack = event.getStack();
        ACTrigger ach = hcPlayerPickup.get(stack.getItem());
        if(ach != null) {
            if(event.player instanceof EntityPlayerMP)
                ach.trigger((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerTransformCategory(TransformCategoryEvent event)
    {
        ACAdvancements.trigger(event.player, ACAdvancements.convert_category.ID);
    }

    @SubscribeEvent
    public void onPlayerLearnSkill(SkillLearnEvent event)
    {
        ACAdvancements.trigger(event.player, ACAdvancements.ac_learning_skill.ID);
    }

    @SubscribeEvent
    public void onSkillExpAdded(SkillExpAddedEvent event)
    {
        if(event.skill.getSkillExp(AbilityData.get(event.player))>=1.0f)
            ACAdvancements.trigger(event.player, ACAdvancements.ac_exp_full.ID);
    }

    @SubscribeEvent
    public void onPlayerOverload(OverloadEvent event)
    {
        ACAdvancements.trigger(event.player, ACAdvancements.ac_overload.ID);
    }
    
    //Init
    
    private DispatcherAch() {
        MinecraftForge.EVENT_BUS.register(this);

    }
    
    //stub method for loading
    public static void init() {
        INSTANCE.rgItemCrafted(AcademyCraftItemList.ITEM_PHASE_GEN, ACAdvancements.phase_generator);
        INSTANCE.rgItemCrafted(AcademyCraftItemList.ITEM_NODE_BASIC, ACAdvancements.ac_node);
        INSTANCE.rgItemCrafted(AcademyCraftItemList.DEVELOPER_PORTABLE, ACAdvancements.ac_developer);
        INSTANCE.rgLevelChange(1, ACAdvancements.dev_category);
        INSTANCE.rgLevelChange(3, ACAdvancements.ac_level_3);
        INSTANCE.rgLevelChange(5, ACAdvancements.ac_level_5);
    }
}