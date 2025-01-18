package cn.academy;

import cn.academy.internal.entity.*;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AcademyCraftEntityList {
    public static final List<Class<? extends Entity>> ENTITY_LIST = new ArrayList<>();

    static {
        ENTITY_LIST.add(EntityMagHook.class);
        ENTITY_LIST.add(EntityMdBall.class);
        ENTITY_LIST.add(EntityBlock.class);
        ENTITY_LIST.add(EntitySilbarn.class);
        ENTITY_LIST.add(EntityCoinThrowing.class);
    }

    private AcademyCraftEntityList() {
    }
}
