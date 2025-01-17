package cn.academy.internal.command;

import cn.academy.AcademyCraft;
import cn.lambdalib2.datapart.PlayerDataTag;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * This is the command used by the client, doesn't specify the player and works on the user.
 * This command will display a warning before you can use it.
 */
public class CommandAIM extends CommandAIMBase {
    static final String ID = "aim_cheats";

    public CommandAIM() {
        super("aim");
    }

    @Override
    public void execute(MinecraftServer svr, ICommandSender commandSender, String[] pars) {
        EntityPlayer player = null;
        try {
            player = getCommandSenderAsPlayer(commandSender);
        } catch (PlayerNotFoundException e) {
            AcademyCraft.LOGGER.warn("Attempt to use command \"aim\" in the console.");
            return;
        }

        if(!isActive(player) && player.getEntityWorld().getWorldInfo().areCommandsAllowed()) {
            setActive(player, true);
        }

        for(int i=0;i<pars.length;i++){pars[i]=pars[i].toLowerCase();}
        if(pars.length == 1) {
            switch(pars[0]) {
                case "cheats_on":
                    setActive(player, true);
                    sendChat(commandSender, locSuccessful());
                    sendChat(commandSender, getLoc("warning"));
                    return;
                case "cheats_off":
                    setActive(player, false);
                    sendChat(commandSender, locSuccessful());
                    return;
                case "?":
                case "help":
                    for(String c : commands)
                    {
                        sendChat(commandSender, getLoc(c));
                    }
                    return;
            }

        }

        if(!isActive(player) && !player.capabilities.isCreativeMode) {
            sendChat(commandSender, getLoc("notactive"));
            return;
        }

        if(pars.length == 0) {
            sendChat(commandSender, getLoc("help"));
            return;
        }

        matchCommands(commandSender, player, pars);
    }

    private void setActive(EntityPlayer player, boolean data) {
        PlayerDataTag.get(player).getTag().setBoolean(ID, data);
    }

    private boolean isActive(EntityPlayer player) {
        return PlayerDataTag.get(player).getTag().getBoolean(ID);
    }
}