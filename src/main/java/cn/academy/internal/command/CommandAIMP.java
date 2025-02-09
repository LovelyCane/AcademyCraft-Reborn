package cn.academy.internal.command;

import cn.academy.api.ability.Category;
import cn.academy.internal.ability.CategoryManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * This is the command for the OPs and server console. You must specify the player name.
 */
public class CommandAIMP extends CommandAIMBase {
    public CommandAIMP() {
        super("aimp");
    }

    @Override
    public void execute(MinecraftServer svr, ICommandSender ics, String[] pars) {
        for (int i = 0; i < pars.length; i++) {
            pars[i] = pars[i].toLowerCase();
        }
        if (pars.length == 0) {
            sendChat(ics, getLoc("help"));
            return;
        }

        //Try to locate the player.
        EntityPlayer player = null;

        //Using player parameter
        player = svr.getPlayerList().getPlayerByUsername(pars[0]);

        if (player != null) {
            String[] newPars = new String[pars.length - 1];
            System.arraycopy(pars, 1, newPars, 0, newPars.length);

            matchCommands(ics, player, newPars);
        } else if (pars[0].equals("catlist")) {
            sendChat(ics, getLoc("cats"));
            List<Category> catList = CategoryManager.INSTANCE.getCategories();
            for (int i = 0; i < catList.size(); ++i) {
                Category cat = catList.get(i);
                sendChat(ics, "#" + i + " " + cat.getName() + ": " + cat.getDisplayName());
            }
        } else if (pars[0].equals("help") || pars[0].equals("?")) {
            for (String c : commands) {
                sendChat(ics, getLoc(c));
            }
        } else {
            sendChat(ics, locNoPlayer());
        }
    }
}