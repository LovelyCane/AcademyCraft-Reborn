package cn.academy.ability.client.ui;

import cn.academy.Resources;
import cn.academy.ability.Skill;
import cn.academy.datapart.AbilityData;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.TextBox;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class SkillPosEditorUI {
    static CGui gui;

    public static CGuiScreen apply() {
        SkillTreeJava.TreeScreen ret = new SkillTreeJava.TreeScreen();
        gui = ret.getGui();

        build();

        gui.listen(SkillTreeJava.RebuildEvent.class, (w, event) -> build());

        return ret;
    }

    private static void build() {
        gui.clear();

        Widget main = SkillTreeJava.initialize(null, gui);

        gui.addWidget(main);

        main.removeWidget("parent_left");

        AbilityData aData = AbilityData.get(Minecraft.getMinecraft().player);
        if (aData.hasCategory()) {
            List<Skill> skills = aData.getCategory().getSkillList();
            for (int idx = 0; idx < skills.size(); idx++) {
                Skill skill = skills.get(idx);
                int y = 5 + idx * 12;

                Widget box0 = new Widget().size(40, 10).pos(20, y).addComponent(Resources.newTextBox(new IFont.FontOption(8)).setContent(skill.getName()));

                Widget box1 = createBox(skill.guiX, newX -> skill.guiX = newX);
                box1.pos(70, y);

                Widget box2 = createBox(skill.guiY, newY -> skill.guiY = newY);
                box2.pos(93, y);

                gui.addWidget(box0);
                gui.addWidget(box1);
                gui.addWidget(box2);
            }
        }
    }

    private static Widget createBox(float init, java.util.function.Consumer<Float> callback) {
        TextBox text = Resources.newTextBox(new IFont.FontOption(8)).setContent(Double.toString(init));
        text.allowEdit();
        Widget ret = new Widget();
        ret.size(20, 10).addComponent(new DrawTexture().setTex(null).setColor(Colors.fromFloat(0.3f, 0.3f, 0.3f, 0.3f))).addComponent(text).listen(TextBox.ConfirmInputEvent.class, (a, evt) -> {
            try {
                float num = Float.parseFloat(text.content);
                callback.accept(num);
                ret.getGui().postEvent(new SkillTreeJava.RebuildEvent());
            } catch (NumberFormatException ignored) {
            }
        });

        return ret;
    }
}
