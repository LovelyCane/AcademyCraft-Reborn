package cn.academy;

import cn.academy.internal.command.CommandAIM;
import cn.academy.internal.command.CommandAIMP;
import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.List;

public class AcademyCraftCommandList {
    public static final List<ICommand> COMMAND_LIST = new ArrayList<>();
    public static final CommandAIM COMMAND_AIM = new CommandAIM();
    public static final CommandAIMP COMMAND_AIMP = new CommandAIMP();

    static {
        COMMAND_LIST.add(COMMAND_AIM);
        COMMAND_LIST.add(COMMAND_AIMP);
    }

    private AcademyCraftCommandList() {
    }
}
