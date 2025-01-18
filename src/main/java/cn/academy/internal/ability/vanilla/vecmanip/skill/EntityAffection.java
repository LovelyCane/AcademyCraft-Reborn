package cn.academy.internal.ability.vanilla.vecmanip.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;

import java.util.*;

/**
 * Handles entity affection of VecDeviation and VecReflection.
 */
public class EntityAffection {

    public static abstract class AffectInfo {}

    public static class Excluded extends AffectInfo {}

    public static class Affected extends AffectInfo {
        private final float difficulty;

        public Affected(float difficulty) {
            this.difficulty = difficulty;
        }

        public float getDifficulty() {
            return difficulty;
        }
    }

    private static final Map<Class<? extends Entity>, Float> entityData;
    private static final Set<Class<? extends Entity>> excluded;

    static {
        entityData = new HashMap<>();
        entityData.put(EntityLivingBase.class, 1.0f);
        entityData.put(EntityMob.class, 1.0f);
        entityData.put(EntityZombie.class, 1.0f);

        // Initialize excluded
        List<String> excludedList = new ArrayList<>();
        excluded = new HashSet<>();
    }

    public static AffectInfo getAffectInfo(Entity entity) {
        for (Class<? extends Entity> klass : excluded) {
            if (klass.isInstance(entity)) {
                return new Excluded();
            }
        }

        for (Map.Entry<Class<? extends Entity>, Float> entry : entityData.entrySet()) {
            if (entry.getKey().isInstance(entity)) {
                return new Affected(entry.getValue());
            }
        }

        return new Affected(1);
    }

    public static void mark(Entity targ) {
        targ.getEntityData().setBoolean("ac_vm_deviated", true);
    }

    public static boolean isMarked(Entity targ) {
        return targ.getEntityData().getBoolean("ac_vm_deviated");
    }
}