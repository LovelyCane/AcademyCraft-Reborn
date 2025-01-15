package cn.academy.internal.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.util.Colors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TechUIWidget extends Widget {
    public Page currentPage;

    public TechUIWidget(Page... pages) {
        List<Page> pageList = Arrays.asList(pages);
        this.currentPage = pageList.get(0);

        this.size(172, 187);
        this.centered();

        for (int idx = 0; idx < pageList.size(); idx++) {
            Page page = pageList.get(idx);

            Widget button = CGUIDocument.read(new ResourceLocation("academy:guis/rework/pageselect.xml")).getWidget("main");
            button.walign(Transform.WidthAlign.LEFT).halign(Transform.HeightAlign.TOP);

            DrawTexture buttonTex = button.getComponent(DrawTexture.class);
            buttonTex.setTex(Resources.getTexture("guis/icons/icon_" + page.id));
            button.scale(0.7f);
            button.pos(-20, idx * 22);

            button.listen(LeftClickEvent.class, (widget, event) -> {
                for (Page p : pages) {
                    p.window.transform.doesDraw = false;
                }
                page.window.transform.doesDraw = true;
                currentPage = page;
            });

            button.listen(FrameEvent.class, (widget, event) -> updateButtonColor(event, page, buttonTex));
            page.window.transform.doesDraw = false;

            this.addWidget(button);
            this.addWidget(page.window);
        }

        pageList.get(0).window.transform.doesDraw = true;
    }

    private void updateButtonColor(FrameEvent evt, Page page, DrawTexture buttonTex) {
        boolean hovering = evt.hovering;
        float alpha = (hovering || this.currentPage == page) ? 1.0f : 0.8f;
        float colorFactor = (this.currentPage == page) ? 1.0f : 0.8f;

        buttonTex.color.setAlpha(Colors.f2i(alpha));
        buttonTex.color.setRed(Colors.f2i(colorFactor));
        buttonTex.color.setGreen(Colors.f2i(colorFactor));
        buttonTex.color.setBlue(Colors.f2i(colorFactor));
    }
}
