package cn.academy.ability.vanilla.vecmanip.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientRuntime;
import cn.academy.ability.context.ContextManager;
import cn.academy.ability.context.DelegateState;
import cn.academy.ability.context.KeyDelegate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StormWing extends Skill {
    public static final StormWing INSTANCE = new StormWing();
    final int STATE_ACTIVE = 1;
    private StormWing() {
        super("storm_wing", 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        rt.addKey(keyid, new KeyDelegate() {
            @Override
            public void onKeyDown() {
                if (currentContext() != null) {
                    currentContext().terminate();
                } else {
                    StormWingContext ctx = new StormWingContext(rt.getEntity());
                    ContextManager.instance.activate(ctx);
                }
            }

            @Override
            public DelegateState getState() {
                if (currentContext() != null) {
                    if (currentContext().getState() == STATE_ACTIVE) {
                        return DelegateState.ACTIVE;
                    } else {
                        return DelegateState.CHARGE;
                    }
                }
                return DelegateState.IDLE;
            }

            private StormWingContext currentContext() {
                return ContextManager.instance.findLocal(StormWingContext.class).orElse(null);
            }

            @Override
            public ResourceLocation getIcon() {
                return StormWing.INSTANCE.getHintIcon();
            }

            @Override
            public int createID() {
                return 0;
            }

            @Override
            public Skill getSkill() {
                return StormWing.INSTANCE;
            }
        });
    }
}