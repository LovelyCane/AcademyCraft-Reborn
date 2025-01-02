package cn.lambdalib2.datapart;

import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.s11n.network.NetworkS11n;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.SideUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A tickable data-storage entity attached to Entity.
 * see {@link EntityData} for access methods.
 * @author WeAthFolD
 */
public abstract class DataPart<T extends Entity> {

    EntityData<T> entityData;
    private boolean syncInit = false;

    boolean needNBTStorage = false;
    boolean needTick = false;
    boolean clientNeedSync = false;
    double serverSyncRange = 10.0;

    // Behaviour

    /**
     * Make this DataPart's tick() method be called every tick. Can be called during runtime or construction.
     */
    protected final void setTick(boolean state) {
        needTick = state;
    }

    /**
     * Make this DataPart to be saved or loaded via NBT when constructed in SERVER.
     */
    protected final void setNBTStorage() {
        needNBTStorage = true;
    }

    /**
     * Make this DataPart to automatically retrieve sync from server when constructed in client.
     */
    protected final void setClientNeedSync() {
        clientNeedSync = true;
    }

    /**
     * @param range The range for other clients to receive sync (if called sync() in server). Can
     *  be called during runtime or construction. Defaults to 10.
     */
    protected final void setServerSyncRange(double range) {
        serverSyncRange = range;
    }

    //

    /**
     * Sync this DataPart's data (fields). If in client, data will be synced to server. Otherwise, data will be synced to any
     *  clients within the range specified by {@link #setServerSyncRange(double)}. The field synchronized follows the
     *  rule of NetworkS11n API.
     */
    @SuppressWarnings("sideonly")
    public final void sync() {
        if (isClient()) {
            __syncClient();
        } else {
//            Debug.log("Send sync " + this);
            ByteBuf buffer = __genSyncBuffer();
            if (buffer.writerIndex() > 0) {
                sendMessage("itn_sync", buffer);
            }

        }
    }

    @SideOnly(Side.CLIENT)
    private void __syncClient() {
        T ent = getEntity();
        if (!(ent instanceof EntityPlayer)) {
            Debug.warn("Trying to call sync() in client for non-EntityPlayers in" + this +
                    ". This usually doesn't make sense.");
        } else if (!(ent.equals(Minecraft.getMinecraft().player))) {
            Debug.warn("Trying to sync non-local player data to server DataPart in " + this +
                    ". This usually doesn't make sense.");
        }

        ByteBuf buffer = __genSyncBuffer();
        if (buffer.writerIndex() > 0) {
            NetworkMessage.sendToServer(this, "itn_sync", buffer);
        }

    }

    private ByteBuf __genSyncBuffer() {
        ByteBuf buf = Unpooled.buffer(512);
        NetworkS11n.serializeRecursively(buf, this, (Class) getClass());
        return buf;
    }

    /**
     * Invoked when this DataPart is made alive.
     */
    public void wake() {

    }

    /**
     * Invoked every tick if {@link #setTick(boolean)} has been invoked with argument true.
     */
    public void tick() {}

    /**
     * Invoked at the synchronized side after one sync process has completed.
     */
    protected void onSynchronized() {}

    /**
     * Stores this DataPart. Called when the DataPart is being stored at SERVER.
     */
    public void toNBT(NBTTagCompound tag) {}

    /**
     * Loads the DataPart. Called when the DataPart is being loaded at SERVER.
     */
    public void fromNBT(NBTTagCompound tag) {}

    /**
     * Get called when player that this part is attached on is dead.
     */
    public void onPlayerDead() {}

    //

    // Utils
    /**
     * @return Whether we are in client.
     */
    protected boolean isClient() {
        return getEntity().world.isRemote;
    }

    protected Side getSide() {
        return isClient() ? Side.CLIENT : Side.SERVER;
    }

    /**
     * @return The entity that this DataPart is attached to.
     */
    public T getEntity() {
        return entityData.getEntity();
    }

    /**
     * @return The {@link EntityData} that handles this entity.
     */
    public EntityData<T> getData() {
        return entityData;
    }

    /**
     * Assert that side is same to parameter and crashes the game if not.
     */
    protected void checkSide(Side side) {
        if (isClient() != side.isClient()) {
            throw new IllegalStateException("Invalid side, expected " + side);
        }
    }

    protected boolean checkSideSoft(Side side) {
        return isClient() == side.isClient();
    }

    protected void debug(Object message) {
        Debug.log(message.toString());
    }

    /**
     * Sends a network message to DataPart instances of other side(s). In server, send to all in range specified
     *  by {@link #setServerSyncRange(double)}.
     */
    protected void sendMessage(String channel, Object ...params) {
        T ent = getEntity();
        if (isClient()) {
            if (!(ent instanceof EntityPlayer)) {
                Debug.warn("Trying to send message in client for non-EntityPlayers in" + this +
                        ". This usually doesn't make sense.");
            } else if (!(ent.equals(Minecraft.getMinecraft().player))) {
                Debug.warn("Trying to send message from non-local player data to server DataPart in " + this +
                        ". This usually doesn't make sense.");
            }

            NetworkMessage.sendToServer(this, channel, params);
        } else {
            NetworkMessage.sendToAllAround(TargetPoints.convert(ent, serverSyncRange), this, channel, params);
        }
    }

    protected void sendToLocal(String channel, Object ...params) {
        if (getEntity() instanceof EntityPlayer) {
            NetworkMessage.sendTo((EntityPlayer) getEntity(), this, channel, params);
        } else {
            throw new IllegalStateException("Not a DataPart of EntityPlayer");
        }
    }

    // Internal

    void callTick() {
        if (isClient() && clientNeedSync && !syncInit) {
            syncInit = true;
            NetworkMessage.sendToServer(this, "itn_query_init", SideUtils.getThePlayer());
        }
        if (needTick) {
            tick();
        }
    }

    @Listener(channel="itn_query_init", side={Side.SERVER})
    private void onQuerySync(EntityPlayerMP client) {
        NetworkMessage.sendTo(client, this, "itn_sync", __genSyncBuffer());
    }

    @Listener(channel="itn_sync", side={Side.CLIENT, Side.SERVER})
    private void onSync(ByteBuf buf) {
//        Debug.log("Sync on " + this);
        NetworkS11n.deserializeRecursivelyInto(buf, this, getClass());
        onSynchronized();
    }
}
