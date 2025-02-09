package cn.academy.internal.command;

import cn.academy.internal.advancements.ACAdvancements;
import cn.academy.internal.util.ACCommand;
import cn.lambdalib2.util.PlayerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * @author EAirPeter
 */
@SuppressWarnings("unused")
public class CommandACACH extends ACCommand {
    public CommandACACH() {
        super("acach");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // /acach ACHIEVEMENT_NAME [PLAYER_NAME]
        String nAch = null;
        String nPlayer = null;
        if (args.length > 0) nAch = args[0];
        if (nAch == null) {
            PlayerUtils.sendChat(sender, "Usage: /acach ACHIEVEMENT_NAME [PLAYER_NAME]");
            return;
        }
        if (args.length > 1) nPlayer = args[1];
        if (nPlayer == null) nPlayer = sender.getName();
        EntityPlayer player = server.getPlayerList().getPlayerByUsername(nPlayer);
        if (player == null) {
            PlayerUtils.sendChat(sender, locNoPlayer());
            return;
        }
        if (ACAdvancements.trigger(player, nAch)) PlayerUtils.sendChat(sender, locSuccessful());
        else PlayerUtils.sendChat(sender, "No such achievement found");
    }
}