package cn.academy.internal.item;

import cn.academy.AcademyCraft;
import cn.academy.internal.terminal.App;
import cn.academy.internal.terminal.AppRegistry;
import cn.academy.internal.terminal.TerminalData;
import cn.lambdalib2.util.Debug;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemInstaller app
 * @author WeAthFolD
 */
public class ItemApp extends Item {
    private static final Map<String, ItemApp> items = new HashMap<>();

    private final String _appName;
    
    private App _app;

    public ItemApp(String name) { // Ctor is called during class construction, so we can't get app at that time.
        _appName = name;

        items.put(_appName, this);
        setCreativeTab(AcademyCraft.CREATIVE_TABS);

    }

    private App getApp() {
        if (_app == null) {
            _app = Debug.assertNotNull(AppRegistry.getByName(_appName), () -> "App not found: " + _appName);
        }
        return _app;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        App app = getApp();
        if(!world.isRemote) {
            TerminalData terminalData = TerminalData.get(player);
            if(!terminalData.isTerminalInstalled()) {
                player.sendMessage(new TextComponentTranslation("ac.terminal.notinstalled"));
            } else if(terminalData.isInstalled(app)) {
                player.sendMessage(
                    new TextComponentTranslation("ac.terminal.app_alrdy_installed", new TextComponentTranslation(app.getDisplayKey()))
                );
            } else {
                if(!player.capabilities.isCreativeMode)
                    stack.shrink(1);
                terminalData.installApp(app);
                player.sendMessage(
                    new TextComponentTranslation("ac.terminal.app_installed", new TextComponentTranslation(app.getDisplayKey()))
                );
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        list.add(getApp().getDisplayName());
    }
}