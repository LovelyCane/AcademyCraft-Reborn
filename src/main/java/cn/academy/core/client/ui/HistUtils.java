package cn.academy.core.client.ui;

import cn.lambdalib2.util.Colors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Color;

import java.util.function.Supplier;

import static cn.academy.core.client.ui.ContainerUI.localHist;

@SideOnly(Side.CLIENT)
public class HistUtils {
    private HistUtils() {
    }

    public static HistElement histEnergy(Supplier<Double> energy, double max) {
        Color color = Colors.fromHexColor(0xff25c4ff);
        Supplier<Double> value = () -> energy.get() / max;
        Supplier<String> desc = () -> String.format("%.2f", energy.get());
        return new HistElement(localHist.get("energy"), color, value, desc);
    }

    public static HistElement histBuffer(Supplier<Double> energy, double max) {
        Color color = Colors.fromHexColor(0xff25f7ff);
        Supplier<Double> value = () -> energy.get() / max;
        Supplier<String> desc = () -> String.format("%.0f IF", energy.get());
        return new HistElement(localHist.get("buffer"), color, value, desc);
    }

    public static HistElement histPhaseLiquid(Supplier<Double> amt, double max) {
        Color color = Colors.fromHexColor(0xff7680de);
        Supplier<Double> value = () -> amt.get() / max;
        Supplier<String> desc = () -> String.format("%.0f mB", amt.get());
        return new HistElement(localHist.get("liquid"), color, value, desc);
    }

    public static HistElement histCapacity(Supplier<Integer> amt, int max) {
        Color color = Colors.fromHexColor(0xffff6c00);
        Supplier<Double> value = () -> (double) amt.get() / max;
        Supplier<String> desc = () -> amt.get() + "/" + max;
        return new HistElement(localHist.get("capacity"), color, value, desc);
    }
}