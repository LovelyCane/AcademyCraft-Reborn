package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VecReflection extends Skill {
    public static VecReflection INSTANCE = new VecReflection();
    public static final String MSG_EFFECT = "effect";
    public static final String MSG_REFLECT_ENTITY = "reflect_ent";
    public static boolean activate;

    public VecReflection() {
        super("vec_reflection", 5);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        rt.addKey(keyID, new KeyDelegate() {
            @Override
            public void onKeyDown() {
                Context<VecReflection> context = findContext();
                if (context != null) {
                    context.terminate();
                } else {
                    ContextManager.instance.activate(new VecReflectionContext(getPlayer()));
                }
            }

            @Override
            public ResourceLocation getIcon() {
                return INSTANCE.getHintIcon();
            }

            @Override
            public int createID() {
                return 0;
            }

            @Override
            public Skill getSkill() {
                return INSTANCE;
            }

            @Override
            public DelegateState getState() {
                return findContext() != null ? DelegateState.ACTIVE : DelegateState.IDLE;
            }

            private Context<VecReflection> findContext() {
                return ContextManager.instance.findLocal(VecReflectionContext.class).orElse(null);
            }
        });
    }
}
