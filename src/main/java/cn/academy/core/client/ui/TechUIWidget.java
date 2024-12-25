package cn.academy.core.client.ui;

import cn.academy.Resources;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.DrawTexture;
import cn.lambdalib2.cgui.component.Transform;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.util.Colors;
import net.minecraft.util.ResourceLocation;

public class TechUIWidget extends Widget {
    private Page currentPage;
    private static final Widget pageButtonTemplate = CGUIDocument.read(new ResourceLocation("academy:guis/rework/pageselect.xml")).getWidget("main");

    public TechUIWidget(Page... pages) {
        this.size(172, 187);
        this.centered();

        if (pages.length == 0) {
            throw new IllegalArgumentException("页面列表不能为空");
        }

        this.currentPage = pages[0];

        for (int idx = 0; idx < pages.length; idx++) {
            Page page = pages[idx];

            Widget button = pageButtonTemplate.copy();
            button.walign(Transform.WidthAlign.LEFT).halign(Transform.HeightAlign.TOP);

            DrawTexture buttonTex = button.getComponent(DrawTexture.class);
            buttonTex.setTex(Resources.getTexture("guis/icons/icon_" + page.getId()));
            button.scale(0.7f);
            button.pos(-20, idx * 22);

            button.listen(LeftClickEvent.class, () -> {
                for (Page p : pages) {
                    p.getWindow().transform.doesDraw = false;
                }
                page.getWindow().transform.doesDraw = true;
                currentPage = page;
            });

            // 设置按钮的颜色变化效果
            button.listen(FrameEvent.class, (widget,evt) -> updateButtonColor(evt, page, buttonTex));

            // 默认隐藏其他页面
            page.getWindow().transform.doesDraw = false;

            // 添加按钮和页面窗口到当前组件
            this.addWidget(button);
            this.addWidget(page.getWindow());
        }

        pages[0].getWindow().transform.doesDraw = true;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    private void updateButtonColor(FrameEvent evt, Page page, DrawTexture buttonTex) {
        float alpha = (evt.hovering || currentPage == page) ? 1.0f : 0.8f;
        float colorFactor = (currentPage == page) ? 1.0f : 0.8f;

        buttonTex.color.setAlpha(Colors.f2i(alpha));
        buttonTex.color.setRed(Colors.f2i(colorFactor));
        buttonTex.color.setGreen(Colors.f2i(colorFactor));
        buttonTex.color.setBlue(Colors.f2i(colorFactor));
    }
}