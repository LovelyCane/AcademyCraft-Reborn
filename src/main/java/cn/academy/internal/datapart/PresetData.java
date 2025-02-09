package cn.academy.internal.datapart;

import cn.academy.internal.ability.Controllable;
import cn.academy.internal.event.ability.CategoryChangeEvent;
import cn.academy.internal.event.ability.PresetUpdateEvent;
import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.nbt.NBTS11n;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.SideUtils;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Preconditions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;

/**
 * Handles preset.
 *
 * @author WeAthFolD
 */
@RegDataPart(EntityPlayer.class)
public class PresetData extends DataPart<EntityPlayer> {
    public static final int MAX_KEYS = 4;
    public static final int MAX_PRESETS = 4;

    private static final String MSG_SYNC_SWITCH = "switch", MSG_SYNC_UPDATE = "update";

    @SerializeIncluded
    int presetID = 0;
    @SerializeIncluded
    Preset[] presets = new Preset[4];

    public PresetData() {
        for (int i = 0; i < MAX_PRESETS; ++i) {
            presets[i] = new Preset();
        }

        setNBTStorage();
        setClientNeedSync();
    }

    // Modifier

    public void clear() {
        checkSide(Side.SERVER);

        for (int i = 0; i < 4; ++i)
            presets[i] = new Preset();

        sync();
    }

    public void setPreset(int id, Preset p) {
        checkSide(Side.SERVER);

        presets[id] = p;
        sync();
    }

    public void switchCurrent(int nid) {
        Preconditions.checkElementIndex(nid, MAX_PRESETS);
        checkSide(Side.SERVER);

        presetID = nid;
        sync();
    }

    // Cross-network

    public void switchFromClient(int id) {
        Preconditions.checkElementIndex(id, MAX_PRESETS);
        checkSide(Side.CLIENT);

        presetID = id;
        sendMessage(MSG_SYNC_SWITCH, id);
    }

    public void setPresetFromClient(int id, Preset p) {
        checkSide(Side.CLIENT);

        presets[id] = p;
        sendMessage(MSG_SYNC_UPDATE, id, p);
        firePresetUpdate();
    }

    //

    // Observer

    public Preset getPreset(int id) {
        return presets[id];
    }

    public int getCurrentID() {
        return presetID;
    }

    public Preset getCurrentPreset() {
        return presets[presetID];
    }

    //

    @Override
    public void fromNBT(NBTTagCompound tag) {
        NBTS11n.read(tag, this);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        NBTS11n.write(tag, this);
    }

    @Listener(channel = MSG_SYNC_SWITCH, side = Side.SERVER)
    private void handleSwitch(int idx) {
        switchCurrent(idx);
    }

    @Listener(channel = MSG_SYNC_UPDATE, side = Side.SERVER)
    private void handleSet(int idx, Preset mapping) {
        setPreset(idx, mapping);
        firePresetUpdate();
    }

    @Override
    protected void onSynchronized() {
        //   debug("OnSynchronized " + isClient() + " " + getCurrentPreset());
        firePresetUpdate();
    }

    private void firePresetUpdate() {
        MinecraftForge.EVENT_BUS.post(new PresetUpdateEvent(getEntity()));
    }

    public static PresetData get(EntityPlayer player) {
        return EntityData.get(player).getPart(PresetData.class);
    }

    public static class Preset {

        public final Controllable[] data;

        public Preset(Controllable[] _data) {
            data = Arrays.copyOf(_data, MAX_KEYS);
        }

        public Preset() {
            data = new Controllable[MAX_KEYS];
            Arrays.fill(data, null);
        }

        public boolean hasMapping(int key) {
            return getControllable(key) != null;
        }

        /**
         * @return The controllable that maps to this key, or null if not present
         */
        public Controllable getControllable(int key) {
            return key >= data.length ? null : data[key];
        }

        public boolean hasControllable(Controllable c) {
            for (Controllable cc : data) {
                if (cc == c) {
                    return true;
                }
            }
            return false;
        }

        public Controllable[] copyData() {
            return Arrays.copyOf(data, MAX_KEYS);
        }

        @Override
        public String toString() {
            ToStringHelper helper = MoreObjects.toStringHelper(this);

            for (int i = 0; i < data.length; ++i) {
                helper.add("#" + i, data[i]);
            }

            return helper.toString();
        }

    }

    public static class Events {
        @SubscribeEvent
        public static void onCategoryChanged(CategoryChangeEvent event) {
            if (!SideUtils.isClient()) {
                PresetData.get(event.player).clear();
            }
        }
    }
}