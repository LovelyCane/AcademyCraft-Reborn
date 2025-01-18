package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;

public class VecReflection extends Skill {
    public static VecReflection INSTANCE = new VecReflection();
    public static final String MSG_EFFECT = "effect";
    public static final String MSG_REFLECT_ENTITY = "reflect_ent";

    public VecReflection() {
        super("vec_reflection", 5);
    }

    @Override
    public void activate(ClientRuntime rt, int keyID) {
        activateSingleKey(rt, keyID, VecReflectionContext::new);
    }
}
