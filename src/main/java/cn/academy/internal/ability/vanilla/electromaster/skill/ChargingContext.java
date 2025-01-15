package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.support.EnergyBlockHelper;
import cn.academy.internal.support.EnergyItemHelper;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.Raytrace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;

import static cn.academy.internal.ability.vanilla.electromaster.skill.ChargingBlockContext.MSG_EFFECT_END;
import static cn.academy.internal.ability.vanilla.electromaster.skill.ChargingBlockContext.MSG_EFFECT_START;
import static cn.lambdalib2.util.MathUtils.lerpf;

public class ChargingContext extends Context<CurrentCharging> {
    private float overload = 0f;
    public final double distance = 15.0d;
    private final float exp = ctx.getSkillExp();
    private final boolean isItem = ctx.player.getHeldEquipment().iterator().hasNext();

    public ChargingContext(EntityPlayer p) {
        super(p, CurrentCharging.INSTANCE);
    }

    public float getChargingSpeed(float exp) {
        return (float) Math.floor(lerpf(15, 35, exp));
    }

    public float getExpIncr(boolean effective) {
        return effective ? 0.0001f : 0.00003f;
    }

    public float getConsumption(float exp) {
        return lerpf(3, 7, exp);
    }

    public float getOverload(float exp) {
        return lerpf(65, 48, exp);
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_onStart() {
        ctx.consume(getOverload(exp), 0);
        overload = ctx.cpData.getOverload();
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_onStart() {
        sendToServer(MSG_EFFECT_START, isItem);
    }

    @Listener(channel = MSG_EFFECT_START, side = Side.SERVER)
    private void s_onEffectStart(Boolean isItem) {
        sendToClient(MSG_EFFECT_START, isItem);
    }

    @Listener(channel = MSG_EFFECT_END, side = Side.SERVER)
    private void s_onEffectEnd(Boolean isItem) {
        sendToClient(MSG_EFFECT_END, isItem);
        terminate();
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_onTick() {
        if (ctx.cpData.getOverload() < overload) {
            ctx.cpData.setOverload(overload);
        }

        if (!isItem) {
            // Perform raytrace
            RayTraceResult pos = Raytrace.traceLiving(player, distance);

            boolean good = false;
            if (pos.typeOfHit == RayTraceResult.Type.BLOCK) {
                TileEntity tile = player.getEntityWorld().getTileEntity(pos.getBlockPos());
                if (EnergyBlockHelper.isSupported(tile)) {
                    good = true;
                    float charge = getChargingSpeed(exp);
                    EnergyBlockHelper.charge(tile, charge, true);
                }
            }

            ctx.addSkillExp(getExpIncr(good));

            if (!ctx.consume(0, getConsumption(exp))) {
                sendToClient(MSG_EFFECT_END, false);
                terminate();
            }

            MovingObjectData mod = new MovingObjectData();
            mod.blockY = pos.getBlockPos().getX();
            mod.blockY = pos.getBlockPos().getY();
            mod.blockZ = pos.getBlockPos().getZ();
            mod.hitVec = pos.hitVec;
            mod.isEntity = pos.typeOfHit == RayTraceResult.Type.ENTITY;
            if (mod.isEntity) {
                mod.entityEyeHeight = pos.entityHit.getEyeHeight();
            }
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            float cp = getConsumption(exp);

            if (ctx.consume(0, cp)) {
                float amt = getChargingSpeed(exp);
                boolean good = EnergyItemHelper.isSupported(stack);
                if (good) {
                    EnergyItemHelper.charge(stack, amt, false);
                }
                ctx.addSkillExp(getExpIncr(good));
            } else {
                sendToClient(MSG_EFFECT_END, true);
                terminate();
            }
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_onEnd() {
        sendToServer(MSG_EFFECT_END, isItem);
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Side.CLIENT)
    private void l_onAbort() {
        sendToServer(MSG_EFFECT_END, isItem);
    }
}
