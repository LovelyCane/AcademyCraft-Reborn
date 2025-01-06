package cn.academy.ability.vanilla.electromaster.skill;

import cn.academy.ability.Skill;
import cn.academy.ability.context.ClientContext;
import cn.academy.ability.context.ClientRuntime;
import cn.academy.ability.context.Context;
import cn.academy.ability.context.RegClientContext;
import cn.academy.internel.auxgui.CurrentChargingHUD;
import cn.academy.internel.sound.ACSounds;
import cn.academy.internel.sound.FollowEntitySound;
import cn.academy.entity.EntityIntensifyEffect;
import cn.lambdalib2.auxgui.AuxGuiHandler;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.academy.ability.vanilla.electromaster.skill.BodyIntensifyJava.IntensifyContext.MSG_EFFECT_END;
import static cn.lambdalib2.util.MathUtils.lerp;
import static cn.lambdalib2.util.MathUtils.lerpf;

public class BodyIntensifyJava extends Skill {
    public static final int MIN_TIME = 10;
    public static final int MAX_TIME = 40;
    public static final int MAX_TOLERANT_TIME = 100;
    public static final String LOOP_SOUND = "em.intensify_loop";
    public static final String ACTIVATE_SOUND = "em.intensify_activate";

    public static final BodyIntensifyJava INSTANCE = new BodyIntensifyJava();

    public static final List<PotionEffect> effects = Arrays.asList(new PotionEffect(Potion.getPotionFromResourceLocation("speed"), 0, 3), new PotionEffect(Potion.getPotionFromResourceLocation("jump_boost"), 0, 1), new PotionEffect(Potion.getPotionFromResourceLocation("regeneration"), 0, 1), new PotionEffect(Potion.getPotionFromResourceLocation("strength"), 0, 1), new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 0, 1));

    public BodyIntensifyJava() {
        super("body_intensify", 3);
    }

    public static PotionEffect createEffect(PotionEffect effect, int level, int duration) {
        return new PotionEffect(effect.getPotion(), duration, Math.min(level, effect.getAmplifier()), effect.getIsAmbient(), true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, IntensifyContext::new);
    }

    public static class IntensifyContext extends Context<BodyIntensifyJava> {
        public static final String MSG_EFFECT_END = "effect_end";
        public static final String MSG_END = "end";

        private int tick = 0;
        private final float consumption = lerpf(20, 15, ctx.getSkillExp());
        private float overload = 0f;

        public IntensifyContext(EntityPlayer p) {
            super(p, BodyIntensifyJava.INSTANCE);
        }

        private double getProbability(int ct) {
            return (ct - 10.0) / 18.0;
        }

        private int getBuffTime(int ct) {
            return (int) (RandUtils.ranged(1, 2) * ct * lerp(1.5, 2.5, ctx.getSkillExp()));
        }

        private int getHungerBuffTime(int ct) {
            return (int) (1.25f * ct);
        }

        private int getBuffLevel(int ct) {
            return (int) Math.floor(getProbability(ct));
        }

        @NetworkMessage.Listener(channel = MSG_MADEALIVE, side = Side.SERVER)
        private void s_consume() {
            overload = lerpf(200, 120, ctx.getSkillExp());
            ctx.consume(overload, 0);
        }

        @NetworkMessage.Listener(channel = MSG_TICK, side = Side.SERVER)
        private void s_onTick() {
            if (ctx.cpData.getOverload() < overload) {
                ctx.cpData.setOverload(overload);
            }
            tick++;
            if ((tick <= MAX_TIME && !ctx.consume(0, consumption)) || tick >= MAX_TOLERANT_TIME) {
                sendToClient(MSG_EFFECT_END, Boolean.FALSE);
                terminate();
            }
        }

        @NetworkMessage.Listener(channel = MSG_END, side = Side.SERVER)
        private void s_onEnd() {
            if (tick >= MIN_TIME) {
                if (tick >= MAX_TIME) {
                    tick = MAX_TIME;
                }

                Collections.shuffle(effects);

                double p = getProbability(tick);
                int i = 0;
                int time = getBuffTime(tick);

                while (p > 0) {
                    double a = RandUtils.ranged(0, 1);
                    if (a < p) {
                        int level = getBuffLevel(tick);
                        i++;
                        player.addPotionEffect(createEffect(effects.get(i), level, time));
                    }
                    p -= 1.0;
                }

                player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("hunger"), getHungerBuffTime(tick), 2));
                ctx.addSkillExp(0.01f);

                int cooldown = (int) lerpf(900, 600, ctx.getSkillExp());
                ctx.setCooldown(cooldown);
                sendToClient(MSG_EFFECT_END, Boolean.TRUE);
                terminate();
            } else {
                sendToClient(MSG_EFFECT_END, Boolean.FALSE);
                terminate();
            }
        }

        @NetworkMessage.Listener(channel = MSG_KEYUP, side = Side.CLIENT)
        private void l_onEnd() {
            sendToServer(MSG_END);
        }

        @NetworkMessage.Listener(channel = MSG_KEYABORT, side = Side.CLIENT)
        private void l_onAbort() {
            sendToSelf(MSG_EFFECT_END, Boolean.FALSE);
            terminate();
        }
    }

    @SideOnly(Side.CLIENT)
    @RegClientContext(IntensifyContext.class)
    public static class IntensifyContextC extends ClientContext {
        private FollowEntitySound loopSound;
        private CurrentChargingHUD hud;

        public IntensifyContextC(Context _parent) {
            super(_parent);
        }

        @NetworkMessage.Listener(channel = MSG_MADEALIVE, side = Side.CLIENT)
        private void c_startEffect() {
            if (isLocal()) {
                loopSound = new FollowEntitySound(player, LOOP_SOUND, SoundCategory.AMBIENT).setLoop();
                hud = new CurrentChargingHUD();
                ACSounds.playClient(loopSound);
                AuxGuiHandler.register(hud);
            }
        }

        @NetworkMessage.Listener(channel = MSG_EFFECT_END, side = Side.CLIENT)
        private void c_endEffect(Boolean performed) {
            if (isLocal()) {
                if (loopSound != null) loopSound.stop();
                if (hud != null) hud.startBlend(performed);
            }

            if (performed) {
                ACSounds.playClient(player, ACTIVATE_SOUND, SoundCategory.AMBIENT, 0.5f);
                player.getEntityWorld().spawnEntity(new EntityIntensifyEffect(player));
            }
        }

        @NetworkMessage.Listener(channel = MSG_TERMINATED, side = Side.CLIENT)
        private void c_terminated() {
            if (loopSound != null) loopSound.stop();
            if (hud != null) hud.startBlend(false);
        }
    }
}
