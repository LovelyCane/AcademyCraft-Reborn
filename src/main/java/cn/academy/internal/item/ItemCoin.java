package cn.academy.internal.item;

import cn.academy.Resources;
import cn.academy.internal.entity.EntityCoinThrowing;
import cn.academy.internal.event.CoinThrowEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KSkun
 */
public class ItemCoin extends Item {
    // Key: PlayerName
    private static final Map<String, EntityCoinThrowing> client = new HashMap<>();
    private static final Map<String, EntityCoinThrowing> server = new HashMap<>();

    public ItemCoin() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        EntityPlayer player = event.player;
        Map<String, EntityCoinThrowing> map = getMap(player);
        EntityCoinThrowing etc = getPlayerCoin(player);
        if (etc != null) {
            if (etc.isDead || etc.world.provider.getDimension() != player.world.provider.getDimension()) {
                map.remove(player.getName());
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getPlayerCoin(player) != null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        //Spawn at both side, not syncing for render effect purpose
        EntityCoinThrowing etc = new EntityCoinThrowing(player, stack, hand);
        world.spawnEntity(etc);

        player.playSound(Resources.sound("entity.flipcoin"), 0.5f, 1.0f);
        setPlayerCoin(player, etc);

        MinecraftForge.EVENT_BUS.post(new CoinThrowEvent(player, etc));
        if (!player.capabilities.isCreativeMode) {
            stack.setCount(stack.getCount() - 1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static EntityCoinThrowing getPlayerCoin(EntityPlayer player) {
        EntityCoinThrowing etc = getMap(player).get(player.getName());
        if (etc != null && !etc.isDead)
            return etc;
        return null;
    }

    public static void setPlayerCoin(EntityPlayer player, EntityCoinThrowing etc) {
        Map<String, EntityCoinThrowing> map = getMap(player);
        map.put(player.getName(), etc);
    }

    private static Map<String, EntityCoinThrowing> getMap(EntityPlayer player) {
        return player.world.isRemote ? client : server;
    }
}