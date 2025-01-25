package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.AbilityContext;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.DelegateState;
import cn.academy.internal.ability.context.KeyDelegate;
import cn.academy.internal.client.renderer.misc.RailgunHandEffect;
import cn.academy.internal.datapart.CPData;
import cn.academy.internal.datapart.PresetData;
import cn.academy.internal.entity.EntityCoinThrowing;
import cn.academy.internal.entity.EntityRailgunFX;
import cn.academy.internal.event.CoinThrowEvent;
import cn.academy.internal.sound.ACSounds;
import cn.academy.internal.util.RangedRayDamage;
import cn.lambdalib2.renderhook.DummyRenderData;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.util.Raytrace;
import cn.lambdalib2.util.SideUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static cn.lambdalib2.util.MathUtils.lerp;
import static cn.lambdalib2.util.MathUtils.lerpf;

public class Railgun extends Skill {
    public static final Railgun INSTANCE = new Railgun();
    public static final String MSG_CHARGE_EFFECT = "charge_eff";
    public static final String MSG_PERFORM = "perform";
    public static final String MSG_REFLECT = "reflect";
    public static final String MSG_COIN_PERFORM = "coin_perform";
    public static final String MSG_ITEM_PERFORM = "item_perform";
    private static final double REFLECT_DISTANCE = 15.0;

    private static boolean hitEntity = false;

    private static final Set<Item> acceptedItems = new HashSet<Item>() {{
        add(Items.IRON_INGOT);
        add(Item.getItemFromBlock(Blocks.IRON_BLOCK));
    }};

    public static boolean isAccepted(ItemStack stack) {
        return stack != null && acceptedItems.contains(stack.getItem());
    }

    public Railgun() {
        super("railgun", 4);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        rt.addKey(keyID, new Delegate());
    }

    @SubscribeEvent
    public void onThrowCoin(CoinThrowEvent evt) {
        CPData cpData = CPData.get(evt.getEntityPlayer());
        PresetData pData = PresetData.get(evt.getEntityPlayer());
        boolean spawn = cpData.canUseAbility() && pData.getCurrentPreset().hasControllable(INSTANCE);

        if (spawn) {
            if (SideUtils.isClient()) {
                informDelegate(evt.coin);
            } else {
                NetworkMessage.sendToAllAround(TargetPoints.convert(evt.getEntityPlayer(), 30), INSTANCE, MSG_CHARGE_EFFECT, evt.getEntityPlayer());
            }
        }
    }

    private void informDelegate(EntityCoinThrowing coin) {
        ClientRuntime rt = ClientRuntime.instance();
        Collection<KeyDelegate> delegates = rt.getDelegates(ClientRuntime.DEFAULT_GROUP);

        if (delegates != null && !delegates.isEmpty()) {
            for (KeyDelegate dele : delegates) {
                if (dele instanceof Delegate) {
                    Delegate rgdele = (Delegate) dele;
                    rgdele.informThrowCoin(coin);
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @NetworkMessage.Listener(channel = MSG_CHARGE_EFFECT, side = Side.CLIENT)
    private void hSpawnClientEffect(EntityPlayer target) {
        spawnClientEffect(target);
    }

    @SideOnly(Side.CLIENT)
    @NetworkMessage.Listener(channel = MSG_REFLECT, side = Side.CLIENT)
    private void hReflectClient(EntityPlayer player, Entity reflector) {
        EntityRailgunFX eff = new EntityRailgunFX(player, REFLECT_DISTANCE);

        double dist = player.getDistance(reflector);
        Vec3d mo = VecUtils.lookingPos(player, dist);
        eff.setPosition(mo.x, mo.y, mo.z);
        eff.rotationYaw = reflector.getRotationYawHead();
        eff.rotationPitch = reflector.rotationPitch;
        player.getEntityWorld().spawnEntity(eff);
    }

    private void reflectServer(EntityPlayer player, Entity reflector) {
        AbilityContext ctx = AbilityContext.of(player, INSTANCE);
        RayTraceResult result = Raytrace.traceLiving(reflector, REFLECT_DISTANCE);
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            ctx.attack(result.entityHit, 14);
            hitEntity = true;
        }

        NetworkMessage.sendToAllAround(TargetPoints.convert(player, 20), Railgun.INSTANCE, MSG_REFLECT, player, reflector);
    }

    @SideOnly(Side.CLIENT)
    private void spawnClientEffect(EntityPlayer target) {
        DummyRenderData.get(target).addRenderHook(new RailgunHandEffect());
    }

    @SideOnly(Side.CLIENT)
    @NetworkMessage.Listener(channel = MSG_PERFORM, side = Side.CLIENT)
    private void performClient(EntityPlayer player, double length) {
        ACSounds.playClient(player.world, player.posX, player.posY, player.posZ, "em.railgun", SoundCategory.AMBIENT, 0.5f, 1.0f);
        player.world.spawnEntity(new EntityRailgunFX(player, length));
    }

    private void performServer(EntityPlayer player) {
        AbilityContext ctx = AbilityContext.of(player, INSTANCE);
        float exp = ctx.getSkillExp();

        float cp = lerpf(200, 450, exp);
        float overload = lerpf(180, 120, exp);
        if (ctx.consume(overload, cp)) {
            float dmg = lerpf(60, 110, exp);
            float energy = lerpf(900, 2000, exp);

            double[] length = {45.0};
            RangedRayDamage damage = new RangedRayDamage.Reflectible(ctx, 2, energy, reflector -> {
                reflectServer(player, reflector);
                length[0] = Math.min(length[0], reflector.getDistance(player));
                NetworkMessage.sendToServer(Railgun.INSTANCE, MSG_REFLECT, player, reflector);
            });
            damage.startDamage = dmg;
            damage.perform();

            if (hitEntity) {
                ctx.addSkillExp(0.01f);
            } else {
                ctx.addSkillExp(0.005f);
            }

            ctx.setCooldown((int) lerp(300, 160, exp));
            NetworkMessage.sendToAllAround(TargetPoints.convert(player, 20), Railgun.INSTANCE, MSG_PERFORM, player, length[0]);
        }
    }

    @NetworkMessage.Listener(channel = MSG_COIN_PERFORM, side = Side.SERVER)
    private void consumeCoinAtServer(EntityPlayer player, EntityCoinThrowing coin) {
        coin.setDead();
        performServer(player);
    }

    @NetworkMessage.Listener(channel = MSG_ITEM_PERFORM, side = Side.SERVER)
    private void consumeItemAtServer(EntityPlayer player) {
        ItemStack equipped = player.getHeldItemMainhand();
        if (isAccepted(equipped)) {
            if (!player.capabilities.isCreativeMode) {
                equipped.setCount(equipped.getCount() - 1);
                if (equipped.getCount() == 0) {
                    player.setHeldItem(EnumHand.MAIN_HAND, null);
                }
            }

            performServer(player);
        }
    }

    private static class Delegate extends KeyDelegate {
        private EntityCoinThrowing coin;
        private int chargeTicks = -1;

        public void informThrowCoin(EntityCoinThrowing _coin) {
            if (coin == null || coin.isDead) {
                coin = _coin;
                onKeyAbort();
            }
        }

        @Override
        public void onKeyDown() {
            if (coin == null) {
                if (Railgun.isAccepted(getPlayer().getHeldItemMainhand())) {
                    Railgun.INSTANCE.spawnClientEffect(getPlayer());
                    chargeTicks = 20;
                }
            } else {
                if (coin.getProgress() > 0.7) {
                    NetworkMessage.sendToServer(Railgun.INSTANCE, MSG_COIN_PERFORM, getPlayer(), coin);
                }
                coin = null;
            }
        }

        @Override
        public void onKeyTick() {
            if (chargeTicks != -1) {
                chargeTicks--;
                if (chargeTicks == 0) {
                    NetworkMessage.sendToServer(Railgun.INSTANCE, MSG_ITEM_PERFORM, getPlayer());
                }
            }
        }

        @Override
        public void onKeyUp() {
            chargeTicks = -1;
        }

        @Override
        public void onKeyAbort() {
            chargeTicks = -1;
        }

        @Override
        public DelegateState getState() {
            if (coin != null && !coin.isDead) {
                if (coin.getProgress() < 0.6) {
                    return DelegateState.CHARGE;
                } else {
                    return DelegateState.ACTIVE;
                }
            } else {
                if (chargeTicks == -1) {
                    return DelegateState.IDLE;
                } else {
                    return DelegateState.CHARGE;
                }
            }
        }

        @Override
        public ResourceLocation getIcon() {
            return Railgun.INSTANCE.getHintIcon();
        }

        @Override
        public int createID() {
            return 0;
        }

        @Override
        public Skill getSkill() {
            return Railgun.INSTANCE;
        }
    }
}