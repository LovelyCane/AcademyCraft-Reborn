package cn.academy.internal.entity;

import cn.academy.AcademyCraft;
import cn.academy.AcademyCraftItemList;
import cn.academy.internal.event.ConfigModifyEvent;
import cn.academy.internal.item.ItemCoin;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.util.PlayerUtils;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.entityx.EntityAdvanced;
import cn.lambdalib2.util.entityx.MotionHandler;
import cn.lambdalib2.util.entityx.handlers.Rigidbody;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author KSkun
 */
public class EntityCoinThrowing extends EntityAdvanced {
    public static boolean PLAY_HEADS_OR_TAILS;
    public EnumHand hand = EnumHand.MAIN_HAND;

    static {
        NetworkS11n.addDirect(EntityCoinThrowing.class, new NetworkS11n.NetS11nAdaptor<EntityCoinThrowing>() {
            @Override
            public void write(ByteBuf buf, EntityCoinThrowing obj) {
                NetworkS11n.serializeWithHint(buf, obj.player, EntityPlayer.class);
            }

            @Override
            public EntityCoinThrowing read(ByteBuf buf) {
                return ItemCoin.getPlayerCoin(NetworkS11n.deserializeWithHint(buf, EntityPlayer.class));
            }
        });
    }

    double yOffset = 0.6;

    private class KeepPosition extends MotionHandler<EntityCoinThrowing> {

        public KeepPosition() {
        }

        @Override
        public void onUpdate() {
            if (EntityCoinThrowing.this.player != null) {
                posX = player.posX;
                posZ = player.posZ;
                if ((posY < player.posY && motionY < 0) || ticksExisted > MAXLIFE) {
                    finishThrowing();
                }
            }

            maxHt = Math.max(maxHt, posY);
        }

        @Override
        public String getID() {
            return "kip";
        }

        @Override
        public void onStart() {
        }

    }

    private static final int MAXLIFE = 120;
    private static final double INITVEL = 0.92;

    private float initHt;
    private double maxHt;

    public EntityPlayer player;

    public ItemStack stack;
    public Vec3d axis;
    public boolean isSync = false;

    @SuppressWarnings("unused")
    public EntityCoinThrowing(World world) {
        super(world);
        isSync = true;
        setup();
    }

    public EntityCoinThrowing(EntityPlayer player, ItemStack is, EnumHand hand) {
        super(player.getEntityWorld());
        this.hand = hand;
        this.stack = is;
        this.player = player;
        this.initHt = (float) player.posY;
        setPosition(player.posX, player.posY, player.posZ);
        this.motionY = player.motionY;
        setup();
        this.ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        if (getEntityWorld().isRemote && isSync)
            setDead();
        super.onUpdate();
    }

    private void setup() {
        Rigidbody rb = new Rigidbody();
        rb.gravity = 0.06;
        this.addMotionHandler(rb);
        this.addMotionHandler(new KeepPosition());
        this.motionY += INITVEL;
        axis = new Vec3d(.1 + rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        this.setSize(0.2F, 0.2F);
    }

    void finishThrowing() {
        if (!getEntityWorld().isRemote && !player.capabilities.isCreativeMode) {
            ItemStack equipped = player.getHeldItem(hand);
            if (equipped.isEmpty()) {
                player.setItemStackToSlot(hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(AcademyCraftItemList.COIN));
            } else if (equipped.getItem() == AcademyCraftItemList.COIN && equipped.getCount() < equipped.getMaxStackSize()) {
                equipped.setCount(equipped.getCount() + 1);
                player.inventory.markDirty();
            } else {
                if (PlayerUtils.mergeStackable(player.inventory, new ItemStack(AcademyCraftItemList.COIN)) == 0) {
                    world.spawnEntity(new EntityItem(world, player.posX, player.posY + yOffset, player.posZ, new ItemStack(AcademyCraftItemList.COIN)));
                }
            }
        }
        if (getEntityWorld().isRemote && PLAY_HEADS_OR_TAILS) {
            player.sendMessage(new TextComponentTranslation("ac.headsOrTails." + RandUtils.nextInt(2)));
        }
        setDead();
    }


    public double getProgress() {
        if (motionY > 0) { //Throwing up
            return (INITVEL - motionY) / INITVEL * 0.5;
        } else {
            return Math.min(1.0, 0.5 + ((maxHt - posY) / (maxHt - initHt)) * 0.5);
        }
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        setDead();
        getEntityWorld().spawnEntity(new EntityItem(world, posX, posY, posZ, new ItemStack(
                AcademyCraftItemList.COIN)));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

    }

    public enum EventListener {
        instance;

        @StateEventCallback
        private static void init(FMLInitializationEvent event) {
            PLAY_HEADS_OR_TAILS = AcademyCraft.config.getBoolean("headsOrTails",
                    "generic", false, "Show heads or tails after throwing a coin.");
            MinecraftForge.EVENT_BUS.register(instance);
        }

        @SubscribeEvent
        public void onConfigModified(ConfigModifyEvent e) {
            PLAY_HEADS_OR_TAILS = AcademyCraft.config.getBoolean("headsOrTails",
                    "generic", false, "Show heads or tails after throwing a coin.");
        }
    }
}