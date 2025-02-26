package cn.academy.internal.event;

import cn.academy.internal.entity.EntityCoinThrowing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * @author WeAthFolD
 */
public class CoinThrowEvent extends PlayerEvent {
    
    public final EntityCoinThrowing coin;

    public CoinThrowEvent(EntityPlayer _player, EntityCoinThrowing _coin) {
        super(_player);
        coin  = _coin;
    }

}