package cn.academy.internal.ability.vanilla.meltdowner;

import cn.academy.api.ability.Category;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.vanilla.VanillaCategories;
import cn.academy.internal.ability.vanilla.meltdowner.passiveskill.RadiationIntensify;
import cn.academy.internal.ability.vanilla.meltdowner.skill.*;

/**
 * @author WeAthFolD
 */

public class CatMeltdowner extends Category {
    public static final Skill electronBomb = ElectronBomb.Instance, radIntensify = RadiationIntensify.INSTANCE, rayBarrage = RayBarrage.INSTANCE, scatterBomb = ScatterBomb.INSTANCE, lightShield = LightShield.INSTANCE, meltdowner = Meltdowner.INSTANCE, jetEngine = JetEngine.INSTANCE, mineRayBasic = MineRayBasic.INSTANCE, mineRayExpert = MineRayExpert.INSTANCE, mineRayLuck = MineRayLuck.INSTANCE, electronMissile = ElectronMissile.INSTANCE;

    public CatMeltdowner() {
        super("meltdowner");
        setColorStyle(126, 255, 132, 80);

        electronBomb.setPosition(15, 45);
        radIntensify.setPosition(35, 75);
        scatterBomb.setPosition(70, 50);
        lightShield.setPosition(55, 15);
        meltdowner.setPosition(115, 40);
        mineRayBasic.setPosition(140, 70);
        rayBarrage.setPosition(140, 10);
        jetEngine.setPosition(170, 32);
        mineRayExpert.setPosition(172, 70);
        mineRayLuck.setPosition(205, 82);
        electronMissile.setPosition(210, 35);

        // Lv1
        this.addSkill(electronBomb);
        this.addSkill(radIntensify);

        // Lv2
        this.addSkill(scatterBomb);
        this.addSkill(lightShield);

        // Lv3
        this.addSkill(meltdowner);
        this.addSkill(mineRayBasic);

        // Lv4
        this.addSkill(rayBarrage);
        this.addSkill(jetEngine);
        this.addSkill(mineRayExpert);

        // Lv5
        this.addSkill(mineRayLuck);
        this.addSkill(electronMissile);

        VanillaCategories.addGenericSkills(this);

        // Deps
        scatterBomb.setParent(electronBomb, 0.8f);
        radIntensify.setParent(electronBomb, 0.5f);
        lightShield.setParent(electronBomb, 1.0f);
        meltdowner.setParent(scatterBomb, 0.8f);
        meltdowner.addSkillDep(lightShield, 0.8f);
        mineRayBasic.setParent(meltdowner, 0.3f);
        rayBarrage.setParent(meltdowner, 0.5f);
        jetEngine.setParent(meltdowner, 1.0f);
        mineRayExpert.setParent(mineRayBasic, 0.8f);
        mineRayLuck.setParent(mineRayExpert, 1.0f);
        electronMissile.setParent(jetEngine, 0.3f);
    }
}