package cn.academy.internal.ability.vanilla.electromaster;

import cn.academy.AcademyCraft;
import cn.academy.api.ability.Category;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.vanilla.VanillaCategories;
import cn.academy.internal.ability.vanilla.electromaster.skill.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CatElectromaster extends Category {
    public static final Skill arcGen = ArcGen.instance,  railgun = Railgun.INSTANCE, magMovement = MagMovement.INSTANCE, currentCharging = CurrentCharging.INSTANCE, bodyIntensify = BodyIntensifyJava.INSTANCE, thunderBolt = ThunderBolt.INSTANCE, thunderClap = ThunderClap.INSTANCE;

    public CatElectromaster() {
        super("electromaster");

        setColorStyle(20, 113, 208, 100);

        arcGen.setPosition(24, 46);
        currentCharging.setPosition(55, 18);
        bodyIntensify.setPosition(97, 15);
        magMovement.setPosition(137, 35);
        thunderBolt.setPosition(86, 67);
        railgun.setPosition(164, 59);
        thunderClap.setPosition(204, 80);

        addSkill(arcGen);
        addSkill(currentCharging);
        addSkill(magMovement);
        addSkill(bodyIntensify);
        addSkill(thunderBolt);
        addSkill(railgun);
        addSkill(thunderClap);

        VanillaCategories.addGenericSkills(this);

        currentCharging.setParent(arcGen, 0.3f);
        magMovement.setParent(arcGen);
        magMovement.addSkillDep(currentCharging, 0.7f);
        bodyIntensify.setParent(arcGen, 1f);
        bodyIntensify.addSkillDep(currentCharging, 1f);
        thunderBolt.setParent(arcGen);
        thunderBolt.addSkillDep(currentCharging, 0.7f);
        railgun.setParent(thunderBolt, 0.3f);
        railgun.addSkillDep(magMovement, 1f);
        thunderClap.setParent(thunderBolt, 1f);
    }

    private static final HashSet<Class<? extends Entity>> metalEntities = new HashSet<>();
    private static final HashSet<Block> metalBlocks = new HashSet<>();

    public static void postInit() {
        Map<String, List<String>> cfgBlocks = AcademyCraft.academyCraftConfig.getAbility().getMetalBlocks();
        Map<String, List<String>> cfgEntities = AcademyCraft.academyCraftConfig.getAbility().getMetalEntities();

        processMetalItems(cfgBlocks, metalBlocks, true);
        processMetalItems(cfgEntities, metalEntities, false);
    }

    @SuppressWarnings("unchecked")
    private static <T> void processMetalItems(Map<String, List<String>> cfgItems, HashSet<T> itemSet, boolean isBlock) {
        for (Map.Entry<String, List<String>> entry : cfgItems.entrySet()) {
            String modid = entry.getKey();
            List<String> items = entry.getValue();
            for (String item : items) {
                ResourceLocation resourceLocation = new ResourceLocation(modid, item);
                if (isBlock) {
                    Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
                    if (block != null) {
                        itemSet.add((T) block);
                    } else {
                        AcademyCraft.LOGGER.error("The block {} is not found!", item);
                    }
                } else {
                    Class<? extends Entity> entityClass = EntityList.getClass(resourceLocation);
                    if (entityClass != null) {
                        itemSet.add((T) entityClass);
                    } else {
                        AcademyCraft.LOGGER.error("The entity {} is not found!", modid + ":" + item);
                    }
                }
            }
        }
    }

    public static boolean isMetalBlock(Block block) {
        return !metalBlocks.contains(block);
    }

    public static boolean isEntityMetallic(Entity entity) {
        if (metalEntities.isEmpty())
            return false;
        for (Class<? extends Entity> entityClass : metalEntities) {
            if (entityClass.isInstance(entity))
                return true;
        }
        return false;
    }
}
