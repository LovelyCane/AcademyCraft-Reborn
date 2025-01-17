package cn.academy.internal.terminal.app.settings;

import cn.academy.internal.event.ConfigModifyEvent;
import cn.lambdalib2.cgui.component.Component;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.cgui.event.GainFocusEvent;
import cn.lambdalib2.cgui.event.IGuiEventHandler;
import cn.lambdalib2.cgui.event.KeyEvent;
import cn.lambdalib2.cgui.event.MouseClickEvent;
import cn.lambdalib2.input.KeyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public class PropertyElements {
    public static class EditKey extends Component {
        static final Color CRL_NORMAL = new Color(200, 200, 200, 200), CRL_EDIT = new Color(251, 133, 37, 200);

        IGuiEventHandler<MouseClickEvent> gMouseHandler;

        String name;
        int value;
        public boolean editing;

        TextBox textBox;

        public EditKey(String name, int value) {
            super("EditKey");
            this.value = value;
            this.name = name;

            listen(KeyEvent.class, (w, event) -> {
                if (editing) {
                    endEditing(event.keyCode);
                }
            });

            listen(GainFocusEvent.class, (w, e) -> startEditing());
        }

        @Override
        public void onAdded() {
            super.onAdded();

            textBox = TextBox.get(widget);
            widget.transform.doesListenKey = true;
            updateKeyName();
        }

        private void updateKeyName() {
            textBox.setContent(KeyManager.getKeyName(value));
        }

        private void startEditing() {
            editing = true;
            textBox.setContent("PRESS");
            textBox.option.color = CRL_EDIT;

            widget.getGui().listen(MouseClickEvent.class, gMouseHandler = (w, event) -> endEditing(event.button - 100));
        }

        private void endEditing(int key) {
            editing = false;
            textBox.option.color = CRL_NORMAL;
            widget.getGui().removeFocus();

            if (key != Keyboard.KEY_ESCAPE) {
                value = key;
            }

            updateKeyName();
            widget.getGui().unlisten(MouseClickEvent.class, gMouseHandler);
            MinecraftForge.EVENT_BUS.post(new ConfigModifyEvent(name, value));
        }
    }
}