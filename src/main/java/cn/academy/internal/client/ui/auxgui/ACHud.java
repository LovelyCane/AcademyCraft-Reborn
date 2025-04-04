package cn.academy.internal.client.ui.auxgui;

import cn.academy.AcademyCraft;
import cn.academy.Resources;
import cn.academy.internal.datapart.CPData;
import cn.lambdalib2.auxgui.AuxGui;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.Transform;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * AC global HUD drawing dispatcher.
 *
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class ACHud extends AuxGui {
    public static final ACHud INSTANCE = new ACHud();
    private final List<Node> nodes = new ArrayList<>();
    private final CGui gui = new CGui();

    static {
        ACHud.INSTANCE.addElement(CPBar.INSTANCE, () -> true, "cpbar", new Widget().size(CPBar.WIDTH, CPBar.HEIGHT).scale(CPBar.SCALE).walign(Transform.WidthAlign.RIGHT).addComponent(new DrawTexture().setTex(Resources.getTexture("guis/edit_preview/cpbar"))));
        ACHud.INSTANCE.addElement(new KeyHintUI(), () -> CPData.get(Minecraft.getMinecraft().player).isActivated(), "keyhint", KeyHintUI.display);
    }

    private ACHud() {
        foreground = false;
    }

    @Override
    public void draw(ScaledResolution sr) {
        gui.resize(sr.getScaledWidth(), sr.getScaledHeight());
        for (Node n : nodes) {
            n.w.transform.doesDraw = n.cond.shows();
        }

        gui.draw();
    }

    public void addElement(Widget w, Condition showCondition, String name, Widget preview) {
        Node node = new Node(w, showCondition, name, preview);
        nodes.add(node);
        node.updatePosition();

        gui.addWidget(w);
    }

    public List<Node> getNodes() {
        return ImmutableList.copyOf(nodes);
    }

    public interface Condition {
        boolean shows();
    }

    public static class Node {
        final Widget w;
        final Condition cond;
        final String name;
        final float defaultX, defaultY;
        final Widget preview;

        public Node(Widget _w, Condition _cond, String _name, Widget _preview) {
            w = _w;
            cond = _cond;
            name = _name;
            defaultX = w.transform.x;
            defaultY = w.transform.y;
            preview = _preview;
        }

        public double[] getPosition() {
            return AcademyCraft.academyCraftConfig.getGui().get(name).getPos();
        }

        public Widget getPreview() {
            return preview;
        }

        public String getName() {
            return name;
        }

        void updatePosition() {
            double[] pos = getPosition();
            w.pos((float) pos[0], (float) pos[1]);
            w.dirty = true;
        }

        public void setPosition(float newX, float newY) {
            AcademyCraft.academyCraftConfig.getGui().get(name).setPos(new double[]{newX, newY});
            updatePosition();
        }
    }
}