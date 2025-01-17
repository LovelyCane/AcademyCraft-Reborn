package cn.academy.internal.ability.vanilla;

import cn.academy.AcademyCraftItemList;
import cn.academy.api.ability.Category;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.RegCategory;
import cn.academy.internal.ability.develop.condition.DevConditionAnySkillOfLevel;
import cn.academy.internal.ability.vanilla.electromaster.CatElectromaster;
import cn.academy.internal.ability.vanilla.generic.skill.SkillBrainCourse;
import cn.academy.internal.ability.vanilla.generic.skill.SkillBrainCourseAdvanced;
import cn.academy.internal.ability.vanilla.generic.skill.SkillMindCourse;
import cn.academy.internal.ability.vanilla.meltdowner.CatMeltdowner;
import cn.academy.internal.ability.vanilla.teleporter.CatTeleporter;
import cn.academy.internal.ability.vanilla.vecmanip.CatVecManip;
import cn.academy.internal.crafting.MetalFormerRecipes;
import cn.academy.internal.tileentity.TileMetalFormer.Mode;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class VanillaCategories {
    @RegCategory
    public static final CatElectromaster electromaster = new CatElectromaster();

    @RegCategory
    public static final CatMeltdowner meltdowner = new CatMeltdowner();

    @RegCategory
    public static final CatTeleporter teleporter = new CatTeleporter();

    @RegCategory
    public static final CatVecManip vecManip = new CatVecManip();

    public static void init() {
        MetalFormerRecipes.INSTANCE.add(new ItemStack(AcademyCraftItemList.REINFORCED_IRON_PLATE), new ItemStack(AcademyCraftItemList.NEEDLE, 6), Mode.INCISE);
        MetalFormerRecipes.INSTANCE.add(new ItemStack(Blocks.RAIL), new ItemStack(AcademyCraftItemList.NEEDLE, 2), Mode.INCISE);
        MetalFormerRecipes.INSTANCE.add(new ItemStack(AcademyCraftItemList.REINFORCED_IRON_PLATE, 2), new ItemStack(AcademyCraftItemList.COIN, 3), Mode.PLATE);
        MetalFormerRecipes.INSTANCE.add(new ItemStack(AcademyCraftItemList.WAFER), new ItemStack(AcademyCraftItemList.SILBARN), Mode.ETCH);
    }

    public static void addGenericSkills(Category category) {
        Skill bc = new SkillBrainCourse(), bca = new SkillBrainCourseAdvanced(), mc = new SkillMindCourse();

        bc.setPosition(30, 110);
        bca.setPosition(115, 110);
        mc.setPosition(205, 110);

        category.addSkill(bc);
        category.addSkill(bca);
        category.addSkill(mc);

        bc.addDevCondition(new DevConditionAnySkillOfLevel(3));

        bca.setParent(bc);
        bca.addDevCondition(new DevConditionAnySkillOfLevel(4));

        mc.setParent(bca);
        mc.addDevCondition(new DevConditionAnySkillOfLevel(5));
    }
}