package cn.lambdalib2.datapart;

import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.s11n.network.NetworkS11n.ContextException;
import cn.lambdalib2.s11n.network.NetworkS11n.NetS11nAdaptor;
import cn.lambdalib2.s11n.network.RegNetS11nAdapter;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.SideUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class EntityData<Ent extends Entity> implements IEntityData {

    private static final String ID = "LL_EntityData";

    private static final List<RegData> regList = new ArrayList<>();
    private static final List<RegData> bothSideList = new ArrayList<>();
    private static final Map<Class<? extends Entity>, Boolean> neededEntityMap = new HashMap<>();

    private static boolean _baked;

    /**
     * Register all the DataPart into List. Use @RegDataPart to set properties, otherwise throw @RuntimeException..
     */
    @SuppressWarnings("unchecked")
    static <T extends Entity> void register(
        Class<? extends DataPart<T>> type,
        EnumSet<Side> sides,
        Predicate<Class<? extends T>> pred) {
        Debug.assert2(!_baked, "Can't register DataPart type after EntityData is used");

        RegData add = new RegData();
        add.type = type;
        add.sides = EnumSet.copyOf(sides);
        add.pred = (Predicate) pred;

        regList.add(add);
        if (add.sides.contains(Side.CLIENT) && add.sides.contains(Side.SERVER)) {
            bothSideList.add(add);
        }
    }

    public static boolean needEntityDataFor(Class<? extends Entity> type) {
        Debug.assert2(_baked);
        if (neededEntityMap.containsKey(type)) {
            return neededEntityMap.get(type);
        }
        boolean need = false;
        for (RegData data : bothSideList) {
            if (data.pred.test(type)) {
                need = true;
                break;
            }
        }

        neededEntityMap.put(type, need);
        return need;
    }

    static void bake() {
        bothSideList.sort(Comparator.comparing(lhs -> lhs.type.getName()));
        Preconditions.checkState(bothSideList.size() < Byte.MAX_VALUE);
        IntStream.range(0, bothSideList.size()).forEach(i -> bothSideList.get(i).networkID = (byte) i);

        Debug.log("EntityData baked, network participants: " +
            bothSideList.stream().map(x -> x.type.getCanonicalName()).collect(Collectors.toList()));
        
        _baked = true;
    }

    private static Capability<IEntityData> getCapability(){
        return Debug.assertNotNull(CapDataPartHandler.DATA_PART_CAPABILITY);
    }

    /**
     * Get entity's EntityData. By get it you can get other data by getPart().
     * @param entity can't be null.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityData<T> get(T entity) {
        Objects.requireNonNull(entity);
        Debug.assert2(_baked);
        if (!needEntityDataFor(entity.getClass()))
            return null;

        IEntityData ret = entity.getCapability(getCapability(),null);
        if (!(ret instanceof EntityData)) {
            throw new RuntimeException("Failed to get EntityData of "+entity+" ret="+ret);
        }

        ((EntityData) ret).checkInit();

        return (EntityData)ret;
    }
    
    // ---------------------------------------------------------

    private final ImmutableMap<Class, DataPart> constructed;

    private Ent entity;

    private boolean _init = false;

    public EntityData(Ent entity) {
        Debug.assertNotNull(entity);
        this.entity = entity;

        // Construct all DataParts
        Map<Class, DataPart> map = new HashMap<>();
        for(RegData data : regList) {
            if (data.isApplicable(entity)) {
                try {
                    DataPart instance = data.type.newInstance();
                    instance.entityData = this;
                    map.put(data.type, instance);
                } catch (IllegalAccessException | InstantiationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        constructed = ImmutableMap.copyOf(map);
    }

    private void checkInit() {
        if (_init)
            return;

        _init = true;
        for (DataPart dp : constructed.values()) {
            dp.wake();
        }
    }
    
    public boolean isInitialized() {
        return entity != null;
    }

    /**
     * @return The datapart of exact type
     * @throws RuntimeException if no such DataPart was registered before
     */
    @SuppressWarnings("unchecked")
    public <T extends DataPart<?>>
    T getPart(Class<T> type) {
        return Debug.assertNotNull(
            (T) constructed.get(type),
            () -> "No DataPart of type " + type + " in " + this
        );
    }

    /**
     * @return The datapart of exact type, or null if not present
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity>
    DataPart<T> getPartNonCreate(Class<? extends DataPart<T>> type) {
        return constructed.getOrDefault(type, null);
    }

    public Ent getEntity() {
        return entity;
    }

    @Override
    public void writeNBT(NBTTagCompound tag_) {
        Debug.assert2(isInitialized());
        NBTTagCompound tag = new NBTTagCompound();
        constructed.values().forEach(part -> {
            if (part.needNBTStorage)
            {
                NBTTagCompound partTag = new NBTTagCompound();
                part.toNBT(partTag);
                tag.setTag(_partNBTID(part), partTag);
            }
        });
        if(tag_.hasKey(ID))
            Debug.warn("Find existed log:"+ID+" when storage NBTTag in EntityData:172.");
        tag_.setTag(ID,tag);
    }

    @Override
    public void readNBT(NBTTagCompound tag_) {
        NBTTagCompound tag = tag_.getCompoundTag(ID);
        for (DataPart dp : constructed.values()) {
            if (dp.needNBTStorage) {
                String id = _partNBTID(dp);
                if (tag.hasKey(id)) {
                    dp.fromNBT(tag.getCompoundTag(id));
                }
            }
        }
    }

    private String _partNBTID(DataPart part) {
        return part.getClass().getCanonicalName();
    }

    private static byte getNetworkID(Class<? extends DataPart> type) {
        for (RegData data : bothSideList) {
            if (data.type == type) {
                return data.networkID;
            }
        }
        throw new IllegalStateException(type + " isn't registered as both side");
    }

    private static Class<? extends DataPart> getTypeFromID(byte id) {
        return bothSideList.get(id).type;
    }

    private boolean needSyncDataPart (DataPart part) {
        boolean need = false;
        for (RegData data : bothSideList) {
            if (data.type == part.getClass() && data.pred.test(this.entity.getClass())) {
                need = true;
                break;
            }
        }
        return need;
    }
    private void tick() {
        checkInit();
        for (DataPart part : constructed.values()) {
            // some Entity DataPart pair only exist in client
//            if (needSyncDataPart(part)) {
                part.callTick();
//            }
        }
    }

    public enum EventListener {
        instance;

        @StateEventCallback
        public static void preInit(FMLPreInitializationEvent event){
            MinecraftForge.EVENT_BUS.register(instance);
        }

        @SubscribeEvent
        public void onLivingUpdate(LivingUpdateEvent evt) {
            EntityData<Entity> data = EntityData.get(evt.getEntityLiving());
            if (data != null) {
                data.tick();
            }
        }

        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent evt) {
            if (evt.getEntityLiving() instanceof EntityPlayer) {
                EntityData<EntityPlayer> playerData = EntityData.get((EntityPlayer) evt.getEntityLiving());
                playerData.constructed.values().forEach(DataPart::onPlayerDead);
            }
        }

        @SubscribeEvent
        public void onPlayerClone(PlayerEvent.Clone evt) {
            EntityData<EntityPlayer> data = EntityData.get(evt.getOriginal());
            if (data != null) {
                // Keep the DataPart instance, re-serialize the data
                Debug.assertNotNull(evt.getEntityPlayer());
                data.entity = evt.getEntityPlayer();
                NBTBase nbt = CapDataPartHandler.storage.writeNBT(getCapability(), evt.getOriginal().getCapability(getCapability(), null), null);
                CapDataPartHandler.storage.readNBT(getCapability(), evt.getEntityPlayer().getCapability(getCapability(), null), null, nbt);
            }
        }
    }

    @RegNetS11nAdapter(EntityData.class)
    public static NetS11nAdaptor<EntityData> adaptor = new NetS11nAdaptor<EntityData>() {
        @Override
        public void write(ByteBuf buf, EntityData obj) {
            NetworkS11n.serializeWithHint(buf, obj.getEntity(), Entity.class);
        }
        @Override
        public EntityData read(ByteBuf buf) throws ContextException {
            Entity living = NetworkS11n.deserializeWithHint(buf, Entity.class);
            if (living != null) {
                return EntityData.get(living);
            } else {
                throw new ContextException("Entity not found");
            }
        }
    };

    @RegNetS11nAdapter(DataPart.class)
    public static NetS11nAdaptor<DataPart> partAdaptor = new NetS11nAdaptor<DataPart>() {
        @Override
        public void write(ByteBuf buf, DataPart obj) {
            NetworkS11n.serializeWithHint(buf, obj.getData(), EntityData.class);
            buf.writeByte(getNetworkID(obj.getClass()));
        }
        @Override
        public DataPart read(ByteBuf buf) throws ContextException {
            EntityData data = NetworkS11n.deserializeWithHint(buf, EntityData.class);
            if (data == null) {
                throw new ContextException("EntityData not found");
            }
            return data.getPart(getTypeFromID(buf.readByte()));
        }
    };

}

class RegData {

    Class<? extends DataPart<?>> type;
    EnumSet<Side> sides;
    Predicate<Class<? extends Entity>> pred;

    byte networkID; // Only useful if created in both sides

    public boolean isApplicable(Entity ent) {
        final Side runtimeSide = SideUtils.getRuntimeSide();
        return sides.contains(runtimeSide) && pred.test(ent.getClass());
    }

}