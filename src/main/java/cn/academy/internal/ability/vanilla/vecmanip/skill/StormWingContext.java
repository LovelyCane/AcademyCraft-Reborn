package cn.academy.internal.ability.vanilla.vecmanip.skill;

import cn.academy.api.ability.Skill;
import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.Context;
import cn.academy.internal.ability.context.DelegateState;
import cn.academy.internal.ability.context.KeyDelegate;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.VecUtils;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

import static cn.lambdalib2.util.MathUtils.lerpf;
import static cn.lambdalib2.util.RandUtils.ranged;
import static cn.lambdalib2.util.VecUtils.entityHeadPos;

@SuppressWarnings("unused")
public class StormWingContext extends Context<StormWing> {
    public static final String MSG_UPDSTATE = "upd_state";
    public static final String MSG_SYNC_STATE = "sync_state";

    public static final String KEY_GROUP = "vm_storm_wing";

    public static final int STATE_CHARGE = 0;
    public static final int STATE_ACTIVE = 1;

    public static final double ACCEL = 0.16;

    private Supplier<Vec3d> currentDir;
    private boolean applying;
    private int keyid;

    private int state = STATE_CHARGE;
    private int stateTick = 0;

    private boolean prevAllowFlying;

    private final double consumption = lerpf(40, 25, ctx.getSkillExp());
    private final double overload = lerpf(10, 7, ctx.getSkillExp());
    private final double speed = (ctx.getSkillExp() < 0.45f ? 0.7f : 1.2f) * lerpf(2, 3, ctx.getSkillExp());
    public final double chargeTime = lerpf(70, 30, ctx.getSkillExp());

    public StormWingContext(EntityPlayer p) {
        super(p, StormWing.INSTANCE);
    }

    public int getState() {
        return state;
    }

    public int getStateTick() {
        return stateTick;
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.SERVER)
    private void s_makeAlive() {
        prevAllowFlying = player.capabilities.allowFlying;
        player.capabilities.allowFlying = true;
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYDOWN, side = Side.CLIENT)
    private void l_keyDown(Supplier<Vec3d> dir, int _keyid) {
        if (state == STATE_ACTIVE) {
            currentDir = dir;
            keyid = _keyid;
            l_syncState();
        }
    }

    @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Side.CLIENT)
    private void l_keyUp(int _keyid) {
        if (currentDir != null && keyid == _keyid) {
            currentDir = null; // Clear direction
            l_syncState(); // Sync state
        }
    }

    private void l_syncState() {
        applying = currentDir != null;
        sendToServer(MSG_UPDSTATE, applying);
    }

    private double move(double from, double to) {
        double delta = to - from;
        return from + Math.min(Math.abs(delta), StormWingContext.ACCEL) * Math.signum(delta);
    }

    @Listener(channel = MSG_UPDSTATE, side = Side.SERVER)
    private void s_update(boolean _applying) {
        applying = _applying;
        sendToExceptLocal(MSG_UPDSTATE, wrap(applying));
    }

    private Object wrap(Object x) {
        return x;
    }

    @Listener(channel = MSG_UPDSTATE, side = Side.CLIENT)
    private void c_update(boolean _applying) {
        applying = _applying;
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void l_tick() {
        if (isLocal()) {
            if (currentDir != null) {
                Vec3d moveDir = currentDir.get();
                Vec3d expectedVel = moveDir.scale(speed);
                if (player.getRidingEntity() != null) {
                    player.dismountRidingEntity();
                }
                player.setVelocity(move(player.motionX, expectedVel.x), move(player.motionY, expectedVel.y), move(player.motionZ, expectedVel.z));
            } else {
                RayTraceResult res = Raytrace.perform(world(), player.getPositionVector().add(0, 0.5, 0), player.getPositionVector().add(0, -0.3, 0), EntitySelectors.nothing());
                if (res.typeOfHit == RayTraceResult.Type.MISS) {
                    player.motionY += 0.078;
                } else {
                    player.motionY = 0.1; // Keep player floating on the air if near ground
                }
            }
            player.fallDistance = 0;
            doConsume();

            if (state == STATE_CHARGE && stateTick > chargeTime) {
                state = STATE_ACTIVE;
                stateTick = 0;
                initKeys();
                sendToServer(MSG_SYNC_STATE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_tick() {
        stateTick++;
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.SERVER)
    private void s_terminate() {
        player.capabilities.allowFlying = prevAllowFlying;
        ctx.setCooldown((int) lerpf(30, 10, ctx.getSkillExp()));
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.SERVER)
    private void s_tick() {
        player.fallDistance = 0;

        if (ctx.getSkillExp() < 0.15f) {
            int checkArea = 10;
            for (int i = 0; i < 40; i++) {
                double rval = ranged(-checkArea, checkArea);
                BlockPos pos = new BlockPos(player.posX + rval, player.posY + rval, player.posZ + rval);
                if (!world().getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                    assert Blocks.AIR != null;
                    world().setBlockState(pos, Blocks.AIR.getDefaultState());
                    world().playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, world().getBlockState(pos).getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1f, false);
                }
            }
        }
    }

    private void doConsume() {
        if (state == STATE_ACTIVE) {
            float expincr = 0.00005f;
            ctx.addSkillExp(expincr);
            ctx.consume((float) overload, (float) consumption);
        }
    }

    @SideOnly(Side.CLIENT)
    public void defKey(int idx, int key, Supplier<Vec3d> dirFactory) {
        clientRuntime().addKey(KEY_GROUP, key, new KeyDelegate() {
            @Override
            public void onKeyDown() {
                sendToSelf(AbilityAPIExt.MSG_KEYDOWN, dirFactory, key);
            }

            @Override
            public void onKeyUp() {
                sendToSelf(AbilityAPIExt.MSG_KEYUP, key);
            }

            @Override
            public void onKeyAbort() {
                onKeyUp();
            }

            @Override
            public ResourceLocation getIcon() {
                return StormWing.INSTANCE.getHintIcon();
            }

            @Override
            public DelegateState getState() {
                return applying && keyid == key ? DelegateState.ACTIVE : DelegateState.IDLE;
            }

            @Override
            public int createID() {
                return idx;
            }

            @Override
            public Skill getSkill() {
                return StormWing.INSTANCE;
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private void initKeys() {
        Minecraft gameSettings = Minecraft.getMinecraft();
        defKey(1, gameSettings.gameSettings.keyBindForward.getKeyCode(), () -> worldSpace(0, 0, 1));
        defKey(2, gameSettings.gameSettings.keyBindBack.getKeyCode(), () -> worldSpace(0, 0, -1));
        defKey(3, gameSettings.gameSettings.keyBindLeft.getKeyCode(), () -> worldSpace(1, 0, 0));
        defKey(4, gameSettings.gameSettings.keyBindRight.getKeyCode(), () -> worldSpace(-1, 0, 0));
    }

    @SideOnly(Side.CLIENT)
    private Vec3d worldSpace(double x, double y, double z) {
        Vec3d moveDir = new Vec3d(x, y, z);
        double yaw = Math.toRadians(player.rotationYawHead);
        double pitch = Math.toRadians(player.rotationPitch);
        return moveDir.rotatePitch((float) -pitch).rotateYaw((float) -yaw);
    }

    @Listener(channel = MSG_SYNC_STATE, side = {Side.CLIENT, Side.SERVER})
    private void syncState() {
        this.state = STATE_ACTIVE;

        if (ctx.getSkillExp() == 1.0f) {
            WorldUtils.getEntities(player, 6, EntitySelectors.everything()).forEach(ent -> {
                double modifier = ranged(0.9, 1.2);
                Vec3d delta = VecUtils.subtract(entityHeadPos(ent), player.getPositionVector()).scale(modifier);
                Vec3d move = delta.normalize().scale(ranged(0.5f, 1.0f));
                VecUtils.setMotion(ent, move);
            });
        }

        if (!isRemote()) {
            sendToExceptLocal(MSG_SYNC_STATE);
        }
    }
}