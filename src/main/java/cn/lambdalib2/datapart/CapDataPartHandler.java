package cn.lambdalib2.datapart;

import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Paindar on 17/10/19.
 */
public class CapDataPartHandler
{
    private static final String TAG_ID = "LL_EntityData";

    @CapabilityInject(IEntityData.class)
    static Capability<IEntityData> DATA_PART_CAPABILITY = null;

    static IStorage<IEntityData> storage = new IStorage<IEntityData>() {
        @Override
        public NBTBase writeNBT(Capability<IEntityData> capability, IEntityData instance, EnumFacing side)
        {
            NBTTagCompound nbt=new NBTTagCompound();
            instance.writeNBT(nbt);
            return nbt;
        }

        @Override
        public void readNBT(Capability<IEntityData> capability, IEntityData instance, EnumFacing side, NBTBase base)
        {
            if (! (instance instanceof EntityData))
                throw new RuntimeException("IEntityData instance does not implement EntityData");
            instance.readNBT((NBTTagCompound) base);
        }
    };

    @SubscribeEvent
    public void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();
        if (!EntityData.needEntityDataFor(entity.getClass()))
            return;
        event.addCapability(new ResourceLocation("lambda_lib"), new ICapabilitySerializable<NBTTagCompound>() {

            EntityData<Entity> _instance;

            private IEntityData getEntityData() {
                if (_instance == null) {
                    _instance = new EntityData<>(entity);
                }
                return _instance;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability.equals(DATA_PART_CAPABILITY);
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                if (DATA_PART_CAPABILITY.equals(capability))
                {
                    @SuppressWarnings("unchecked")
                    T result = (T) getEntityData();
                    return result;
                }
                return null;
            }

            @Override
            public NBTTagCompound serializeNBT()
            {
                NBTTagCompound nbt=new NBTTagCompound();
                nbt.setTag(TAG_ID,storage.writeNBT(DATA_PART_CAPABILITY, getEntityData(),null));
                return nbt;
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt)
            {
                NBTTagCompound tag= nbt.getCompoundTag(TAG_ID);
                storage.readNBT(DATA_PART_CAPABILITY, getEntityData(), null, tag);
            }
        });
    }

    @StateEventCallback
    public static void register(FMLPreInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new CapDataPartHandler());
        CapabilityManager.INSTANCE.register(IEntityData.class, storage, () -> { throw new RuntimeException("Doesn't provide default instance"); });
    }
}
