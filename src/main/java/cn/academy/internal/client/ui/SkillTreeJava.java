package cn.academy.internal.client.ui;

import cn.academy.AcademyCraftItemList;
import cn.academy.Resources;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.AbilityLocalization;
import cn.academy.internal.ability.develop.DevelopData;
import cn.academy.internal.ability.develop.IDeveloper;
import cn.academy.internal.ability.develop.LearningHelper;
import cn.academy.internal.ability.develop.action.DevelopActionLevel;
import cn.academy.internal.ability.develop.action.DevelopActionSkill;
import cn.academy.internal.ability.develop.condition.IDevCondition;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.util.LocalHelper;
import cn.lambdalib2.cgui.CGui;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.GuiEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.render.font.IFont;
import cn.lambdalib2.render.font.IFont.FontAlign;
import cn.lambdalib2.render.font.IFont.FontOption;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.Colors;
import cn.lambdalib2.util.GameTimer;
import cn.lambdalib2.util.HudUtils;
import cn.lambdalib2.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.lambdalib2.util.MathUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class SkillTreeJava {
    public static final Widget template = CGUIDocument.read(Resources.getGui("rework/page_developer")).getWidget("main");

    public static final ResourceLocation texAreaBack = Resources.preloadTexture("guis/effect/effect_developer_background");
    public static final ResourceLocation texSkillBack = Resources.preloadMipmapTexture("guis/developer/skill_back");
    public static final ResourceLocation texSkillMask = Resources.preloadMipmapTexture("guis/developer/skill_radial_mask");
    public static final ResourceLocation texSkillOutline = Resources.preloadMipmapTexture("guis/developer/skill_outline");
    public static final ResourceLocation texLine = Resources.preloadMipmapTexture("guis/developer/line");
    public static final ResourceLocation texViewOutline = Resources.preloadMipmapTexture("guis/developer/skill_view_outline");
    public static final ResourceLocation texViewOutlineGlow = Resources.preloadMipmapTexture("guis/developer/skill_view_outline_glow");
    public static final ResourceLocation texButton = Resources.getTexture("guis/developer/button");

    public static final FontOption foSkillTitle = new FontOption(12, FontAlign.CENTER);
    public static final FontOption foSkillDesc = new FontOption(9, FontAlign.CENTER);
    public static final FontOption foSkillProg = new FontOption(8, FontAlign.CENTER, Colors.fromHexColor(0xffa1e1ff));
    public static final FontOption foSkillUnlearned = new FontOption(10, FontAlign.CENTER, Colors.fromHexColor(0xffff5555));
    public static final FontOption foSkillUnlearned2 = new FontOption(10, FontAlign.CENTER, Colors.fromHexColor(0xaaffffff));
    public static final FontOption foSkillReq = new FontOption(9, FontAlign.RIGHT, Colors.fromHexColor(0xaaffffff));
    public static final FontOption foSkillReqDetail = new FontOption(9, FontAlign.LEFT, Colors.fromHexColor(0xeeffffff));
    public static final FontOption foSkillReqDetail2 = new FontOption(9, FontAlign.LEFT, Colors.fromHexColor(0xffee5858));
    public static final FontOption foLevelTitle = new FontOption(12, FontAlign.CENTER);
    public static final FontOption foLevelReq = new FontOption(9, FontAlign.CENTER);

    public static final IFont Font = Resources.font();
    public static final IFont FontBold = Resources.fontBold();

    public static final LocalHelper local = LocalHelper.at("ac.skill_tree");

    public static final EntityPlayerSP player = Minecraft.getMinecraft().player;

    // Constants
    public static final int StateIdle = 0;
    public static final int StateHover = 1;
    public static final double TransitTime = 0.1;
    public static final float WidgetSize = 16.0f;
    public static final float ProgSize = 31.0f;
    public static final float TotalSize = 23.0f;
    public static final float IconSize = 14.0f;

    // Scaling factors
    public static final double back_scale = 1.01;
    public static final double back_scale_inv = 1 / back_scale;
    public static final double max_du = back_scale - 1;
    public static final int max_du_skills = 10;
    public static float dx = 0.0f;
    public static float dy = 0.0f;

    // Methods
    public static double scale(double x) {
        return (x - 0.5) * back_scale_inv + 0.5;
    }

    public static double[] center(double x, double y) {
        double[] result = new double[2];
        result[0] = x + WidgetSize / 2;
        result[1] = y + WidgetSize / 2;
        return result;
    }

    public static class TreeScreen extends CGuiScreen {
        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }

    public static class Cover extends Component {
        private double lastTransit = GameTimer.getTime();
        private boolean ended = false;

        public Cover() {
            super("cover");

            this.listen(FrameEvent.class, (a, evt) -> {
                double time = GameTimer.getTime();
                double dt = time - lastTransit;
                widget.transform.width = widget.getGui().getWidth();
                widget.transform.height = widget.getGui().getHeight();

                double src = clampd(0, 1, dt / 0.2);
                double alpha = ended ? 1 - src : src;

                glColor4d(0, 0, 0, alpha * 0.7);
                HudUtils.colorRect(0, 0, widget.transform.width, widget.transform.height);

                if (ended && alpha == 0) {
                    widget.post(new CloseEvent());
                    widget.dispose();
                }

                widget.dirty = true;
            });
        }

        public void end() {
            ended = true;
            lastTransit = GameTimer.getTime();
        }
    }

    public static class RebuildEvent implements GuiEvent {
    }

    public static class CloseEvent implements GuiEvent {
    }

    public static Widget initialize(IDeveloper developer, CGui gui) {
        Widget ret = template.copy();
        AbilityData aData = AbilityData.get(player);
        DevelopData devData = DevelopData.get(player);
        Widget area = ret.getWidget("parent_right/area");

        if (!aData.hasCategory()) {
            send(NetDelegateJava.MSG_START_LEVEL, devData, developer);
            devData.reset();
        } else if (Optional.of(player.getHeldItem(EnumHand.MAIN_HAND)).map(item -> item.getItem() == AcademyCraftItemList.MAGNETIC_COIL).orElse(false)) {
            send(NetDelegateJava.MSG_RESET, devData, developer);
            devData.reset();
        } else {
            // Initialize skill area
            area.listen(FrameEvent.class, (a, evt) -> {
                CGui areaGui = area.getGui();
                dx = clampf(0, 1, areaGui.getMouseX() / areaGui.getWidth()) - 0.5f;
                dy = clampf(0, 1, areaGui.getMouseY() / areaGui.getHeight()) - 0.5f;

                // Draw background
                RenderUtils.loadTexture(texAreaBack);
                HudUtils.rawRect(0, 0, scale(dx * max_du), scale(dy * max_du), area.transform.width, area.transform.height, back_scale_inv, back_scale_inv);
            });

            if (aData.hasCategory()) {
                List<Skill> skills = aData.getCategory().getSkillList();
                int idx = 0;
                for (Skill skill : skills) {
                    float ProgAlign = (TotalSize - ProgSize) / 2;
                    float Align = (TotalSize - IconSize) / 2;
                    float DrawAlign = (WidgetSize - TotalSize) / 2;
                    boolean learned = aData.isSkillLearned(skill);
                    Widget widget = new Widget();
                    float sx = skill.guiX;
                    float sy = skill.guiY;
                    final double[] lastTransit = {GameTimer.getTime() - 2};
                    final int[] state = {StateIdle};
                    double creationTime = GameTimer.getTime();
                    float blendOffset = idx * 0.08f + 0.1f;
                    double mAlpha = (learned) ? 1.0 : (aData.isSkillLearned(skill)) ? 0.7 : 0.25;

                    widget.pos(sx, sy).size(WidgetSize, WidgetSize);
                    widget.listen(FrameEvent.class, (a, evt) -> {
                        double time = GameTimer.getTime();
                        widget.pos(sx - dx * max_du_skills, sy - dy * max_du_skills);
                        widget.dirty = true;
                        double transitProgress = clampd(0, 1, (time - lastTransit[0]) / TransitTime);
                        double scale = (state[0] == StateIdle) ? lerp(1.2f, 1f, clampd(0, 1, transitProgress)) : lerp(1f, 1.2f, clampd(0, 1, transitProgress));

                        if (transitProgress == 1) {
                            if (state[0] == StateIdle && evt.hovering) {
                                state[0] = StateHover;
                                lastTransit[0] = GameTimer.getTime();
                            } else if (state[0] == StateHover && !evt.hovering) {
                                state[0] = StateIdle;
                                lastTransit[0] = GameTimer.getTime();
                            }
                        }

                        double dt = Math.max(0, time - creationTime - blendOffset);
                        double backAlpha = mAlpha * clampd(0, 1, dt * 10.0);
                        double iconAlpha = mAlpha * clampd(0, 1, (dt - 0.08) * 10.0);
                        double lineBlend = clampd(0, 1, dt * 5.0);

                        glEnable(GL_DEPTH_TEST);
                        glPushMatrix();
                        if (skill.getParent() != null) {
                            Skill parent = skill.getParent();
                            double[] centerSkill = center(skill.guiX, skill.guiY);
                            double[] centerParent = center(parent.guiX, parent.guiY);
                            float px = (float) (centerParent[0] - centerSkill[0]);
                            float py = (float) (centerParent[1] - centerSkill[1]);
                            double norm = Math.sqrt(px * px + py * py);
                            float dxLine = (float) (px / norm * 12.2);
                            float dyLine = (float) (py / norm * 12.2);

                            drawLine(px + WidgetSize / 2 - dxLine, py + WidgetSize / 2 - dyLine, WidgetSize / 2 + dxLine, WidgetSize / 2 + dyLine, 5.5, mAlpha, lineBlend);
                        }
                        glTranslated(DrawAlign, DrawAlign, 10);
                        glTranslated(TotalSize / 2, TotalSize / 2, 0);
                        glScaled(scale, scale, 1);
                        glTranslated(-TotalSize / 2, -TotalSize / 2, 0);

                        // Draw back without depth writing
                        glColor4d(1, 1, 1, backAlpha);
                        glDepthMask(false);
                        RenderUtils.loadTexture(texSkillBack);
                        HudUtils.rect(0, 0, TotalSize, TotalSize);

                        // Draw back as a depth mask
                        glDepthMask(true);
                        glEnable(GL_ALPHA_TEST);
                        glColorMask(false, false, false, false);
                        glAlphaFunc(GL_GREATER, 0.3f);
                        RenderUtils.loadTexture(texSkillBack);
                        HudUtils.rect(0, 0, TotalSize, TotalSize);

                        glDisable(GL_ALPHA_TEST);
                        glColorMask(true, true, true, true);
                        glDepthMask(false);

                        // Draw skill
                        glColor4d(1, 1, 1, iconAlpha);
                        glDepthFunc(GL_EQUAL);
                        RenderUtils.loadTexture(skill.getHintIcon());
                        HudUtils.rect(Align, Align, IconSize, IconSize);

                        glDepthFunc(GL_LEQUAL);

                        // Progress bar (if learned)
                        glColor4d(1, 1, 1, 1);
                        if (learned) {
                            glDisable(GL_DEPTH_TEST);
                            glActiveTexture(GL_TEXTURE0);
                            RenderUtils.loadTexture(texSkillOutline);
                            glActiveTexture(GL_TEXTURE1);
                            int texture1Binding = glGetInteger(GL_TEXTURE_BINDING_2D);
                            RenderUtils.loadTexture(texSkillMask);
                            HudUtils.rect(ProgAlign, ProgAlign, ProgSize, ProgSize);
                            glBindTexture(GL_TEXTURE_2D, texture1Binding);
                            glActiveTexture(GL_TEXTURE0);
                            glEnable(GL_DEPTH_TEST);
                        }

                        glPopMatrix();
                        glDepthFunc(GL_NOTEQUAL);
                        glPushMatrix();
                        glPopMatrix();
                        glDepthFunc(GL_LEQUAL);
                        glDisable(GL_DEPTH_TEST);
                    });

                    widget.listen(LeftClickEvent.class, () -> {
                        Widget cover = skillViewArea(skill, aData, gui, developer);
                        widget.getGui().addWidget(cover);
                    });

                    area.addWidget(widget);
                    idx++;
                }
            }
        }

        // Initialize left ability panel
        {
            Widget panel = ret.getWidget("parent_left/panel_ability");
            ResourceLocation icon;
            String name;
            float prog;
            if (aData.getCategoryNullable() != null) {
                icon = aData.getCategory().getDeveloperIcon();
                name = aData.getCategory().getDisplayName();
                prog = Math.max(0.02f, aData.getLevelProgress());
            } else {
                icon = Resources.getTexture("guis/icons/icon_nocategory");
                name = "N/A";
                prog = 0.0f;
            }

            panel.getWidget("logo_ability").getComponent(DrawTexture.class).setTex(icon);
            panel.getWidget("text_abilityname").getComponent(TextBox.class).setContent(name);
            panel.getWidget("logo_progress").getComponent(ProgressBar.class).progress = (prog);
            panel.getWidget("text_level").getComponent(TextBox.class).setContent(AbilityLocalization.instance.levelDesc(aData.getLevel()));
            panel.getWidget("text_exp").getComponent(TextBox.class).setContent("EXP " + (aData.getLevelProgress() * 100) + "%");

            if (developer != null && aData.hasCategory() && LearningHelper.canLevelUp(developer.getType(), aData)) {
                Widget btn = panel.getWidget("btn_upgrade");
                btn.transform.doesDraw = true;
                btn.listen(LeftClickEvent.class, () -> {
                    Widget cover = levelUpArea(aData, gui, developer);
                    gui.addWidget(cover);
                });
                panel.removeWidget("text_level");
            }
        }

        // Initialize machine panel
        {
            Widget panel = ret.getWidget("parent_left/panel_machine");
            Widget wProgPower = panel.getWidget("progress_power");
            ProgressBar progPower = wProgPower.getComponent(ProgressBar.class);
            Widget wProgRate = panel.getWidget("progress_syncrate");
            ProgressBar progRate = wProgRate.getComponent(ProgressBar.class);

            if (developer != null) {
                wProgPower.listen(FrameEvent.class, () -> progPower.progress = (developer.getEnergy() / developer.getMaxEnergy()));
                progRate.progress = (developer.getType().syncRate);

                panel.getWidget("button_wireless").transform.doesDraw = false;
                panel.getWidget("text_wireless").transform.doesDraw = false;
            } else {
                ret.getWidget("parent_left/ui_left").getComponent(DrawTexture.class).setTex(Resources.getTexture("guis/ui/ui_developerleft_skilltree"));
                panel.transform.doesDraw = false;
            }
        }

        return ret;
    }

    public static Widget skillViewArea(Skill skill, AbilityData data, CGui gui, IDeveloper developer) {
        Widget ret = blackCover(gui);
        Widget skillWid = new Widget();
        skillWid.centered().size(50, 50);
        boolean learned = data.isSkillLearned(skill);
        AtomicBoolean canClose = new AtomicBoolean(true);
        AtomicBoolean shouldRebuild = new AtomicBoolean(false);
        Widget textArea = new Widget().size(0, 10).centered().pos(0, 25);
        if (learned) {
            skillWid.listen(FrameEvent.class, () -> drawActionIcon(skill.getHintIcon(), false));
            textArea.listen(FrameEvent.class, () -> {
                FontBold.draw(skill.getDisplayName(), 0, 3, foSkillTitle);
                Font.draw(local.get("skill_exp") + (data.getSkillExp(skill) * 100), 0, 15, foSkillProg);
                Font.drawSeperated(skill.getDescription(), 0, 24, 200, foSkillDesc);
            });
        } else {
            AtomicReference<Double> progress = new AtomicReference<>((double) 0);
            AtomicReference<Optional<String>> message = new AtomicReference<>(Optional.empty());
            skillWid.listen(FrameEvent.class, () -> drawActionIcon(skill.getHintIcon(), progress.get() == 1));
            String skillNameText = skill.getDisplayName() + " (LV " + skill.getLevel() + ")";
            textArea.listen(FrameEvent.class, () -> {
                FontBold.draw(skillNameText, 0, 3, foSkillTitle);
                Font.draw(local.get("skill_not_learned"), 0, 15, foSkillUnlearned);
            });
            if (developer != null) {
                DevelopActionSkill action = new DevelopActionSkill(skill);
                double estmCons = LearningHelper.getEstimatedConsumption(player, developer.getType(), action);
                List<IDevCondition> conditions = skill.getDevConditions().stream().filter(IDevCondition::shouldDisplay).collect(Collectors.toList());
                int CondIconSize = 14;
                int CondIconStep = 16;
                int len = CondIconStep * conditions.size();
                textArea.listen(FrameEvent.class, () -> Font.draw(local.get("req"), -len / 2 - 2, 28, foSkillReq));
                class CondTag extends Component {
                    final IDevCondition cond;
                    final boolean accepted;

                    CondTag(IDevCondition cond, boolean accepted) {
                        super("CondTag");
                        this.cond = cond;
                        this.accepted = accepted;
                    }
                }
                int idx = 0;
                for (IDevCondition cond : conditions) {
                    Widget widget = new Widget().size(CondIconSize, CondIconSize).pos(-len / 2 + CondIconStep * idx++, 25).size(CondIconSize, CondIconSize);
                    DrawTexture tex = new DrawTexture(cond.getIcon());
                    boolean accepted = cond.accepts(data, developer, skill);
                    widget.addComponent(tex);
                    widget.addComponent(new CondTag(cond, accepted));
                    textArea.addWidget(widget);
                }
                textArea.listen(FrameEvent.class, () -> {
                    Optional<Widget> w = Optional.ofNullable(gui.getHoveringWidget());
                    w.ifPresent(widget -> {
                        Optional<CondTag> tag = Optional.ofNullable(widget.getComponent(CondTag.class));
                        tag.ifPresent(condTag -> Font.draw("(" + condTag.cond.getHintText() + ")", len / 2 + 3, 27, condTag.accepted ? foSkillReqDetail : foSkillReqDetail2));
                    });
                });
                textArea.listen(FrameEvent.class, () -> {
                    message.get().ifPresent(str -> Font.draw(str, 0, 40, foSkillUnlearned2));
                    if (!message.get().isPresent()) {
                        Font.draw(String.format(String.format(String.valueOf(estmCons))), 0, 40, foSkillUnlearned2);
                    }
                });
                Widget button = newButton().centered().pos(0, 55);
                button.listen(LeftClickEvent.class, () -> {
                    if (developer.getEnergy() < estmCons) {
                        message.set(Optional.of(local.get("noenergy")));
                    } else if (skill.getLevel() > data.getLevel()) {
                        message.set(Optional.of(local.getFormatted("level_fail", skill.getLevel())));
                    } else if (!action.validate(player, developer)) {
                        message.set(Optional.of(local.get("condition_fail")));
                    } else {
                        DevelopData devData = DevelopData.get(player);
                        devData.reset();
                        send(NetDelegateJava.MSG_START_SKILL, devData, developer, skill);
                        canClose.set(false);
                        ret.listen(FrameEvent.class, () -> {
                            switch (devData.getState()) {
                                case IDLE:
                                    break;
                                case DEVELOPING:
                                    message.set(Optional.of(local.get("progress") + devData.getDevelopProgress() * 100));
                                    progress.set(devData.getDevelopProgress());
                                    break;
                                case DONE:
                                    message.set(Optional.of(local.get("dev_successful")));
                                    shouldRebuild.set(true);
                                    progress.set(1.0);
                                    canClose.set(true);
                                    break;
                                case FAILED:
                                    canClose.set(true);
                                    message.set(Optional.of(local.get("dev_failed")));
                                    break;
                            }
                        });
                    }
                    button.dispose();
                });
                textArea.addWidget(button);
            }
        }
        ret.addWidget(textArea);
        ret.addWidget(skillWid);
        ret.listen(LeftClickEvent.class, () -> {
            if (canClose.get()) {
                if (shouldRebuild.get()) {
                    gui.postEvent(new RebuildEvent());
                } else {
                    ret.getComponent(Cover.class).end();
                }
            }
        });
        return ret;
    }

    public static Widget blackCover(CGui gui) {
        Widget ret = new Widget();
        ret.addComponents(new Cover());
        ret.size(gui.getWidth(), gui.getHeight());
        return ret;
    }


    public static Widget levelUpArea(AbilityData data, CGui gui, IDeveloper developer) {
        Widget ret = blackCover(gui);

        // Create a new widget
        Widget wid = new Widget();
        wid.centered().size(50, 50);

        DevelopActionLevel action = new DevelopActionLevel();
        double estmCons = LearningHelper.getEstimatedConsumption(player, developer.getType(), action);

        Widget textArea = new Widget().size(0, 10).centered().pos(0, 25);

        AtomicReference<String> hint = new AtomicReference<>(local.get("level_question"));
        AtomicReference<Double> progress = new AtomicReference<>((double) 0);
        AtomicBoolean canClose = new AtomicBoolean(true);
        AtomicBoolean shouldRebuild = new AtomicBoolean(false);

        // Get the icon texture
        ResourceLocation icon = Resources.getTexture("abilities/condition/any" + (data.getLevel() + 1));

        // Use custom event listener for frame updates
        wid.listen(FrameEvent.class, () -> drawActionIcon(icon, progress.get() == 1));

        String lvltext = local.getFormatted("uplevel", AbilityLocalization.instance.levelDesc(data.getLevel() + 1));
        String reqtext = local.get("req") + String.format("%.0f", estmCons);
        textArea.listen(FrameEvent.class, () -> {
            Font.draw(lvltext, 0, 3, foLevelTitle);
            Font.draw(reqtext, 0, 16, foLevelReq);
            Font.draw(hint.get(), 0, 26, foLevelReq);
        });

        // Create a new button and add event listener for left click
        Widget button = newButton().centered().pos(0, 40);
        button.listen(LeftClickEvent.class, () -> {
            if (developer.getEnergy() < estmCons) {
                hint.set(local.get("noenergy"));
            } else {
                DevelopData devData = DevelopData.get(player);
                devData.reset();
                canClose.set(false);

                send(NetDelegateJava.MSG_START_LEVEL, devData, developer);
                ret.listen(FrameEvent.class, () -> {
                    switch (devData.getState()) {
                        case IDLE:
                            // Do nothing
                            break;

                        case DEVELOPING:
                            hint.set(local.get("dev_developing"));
                            progress.set(devData.getDevelopProgress());
                            break;

                        case DONE:
                            hint.set(local.get("dev_successful"));
                            progress.set(1.0);
                            canClose.set(true);
                            shouldRebuild.set(true);
                            break;

                        case FAILED:
                            hint.set(local.get("dev_failed"));
                            canClose.set(true);
                            break;
                    }
                });
            }

            button.dispose();
        });

        textArea.addWidget(button);
        ret.addWidget(textArea);
        ret.listen(LeftClickEvent.class, () -> {
            if (canClose.get()) {
                if (shouldRebuild.get()) {
                    gui.postEvent(new RebuildEvent());
                } else {
                    ret.getComponent(Cover.class).end();
                }
            }
        });

        ret.addWidget(wid);

        return ret;
    }

    private static void drawLine(double x0, double y0, double x1, double y1, double width, double alpha, double progress) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double norm = Math.sqrt(dx * dx + dy * dy);
        double nx = -dy / norm / 2 * width;
        double ny = dx / norm / 2 * width;

        double xx = lerp(x0, x1, progress);
        double yy = lerp(y0, y1, progress);

        RenderUtils.loadTexture(texLine);
        glColor4d(1, 1, 1, alpha);

        glBegin(GL_QUADS);

        glTexCoord2d(0, 0);
        glVertex2d(x0 - nx, y0 - ny);

        glTexCoord2d(0, 1);
        glVertex2d(x0 + nx, y0 + ny);

        glTexCoord2d(1, 1);
        glVertex2d(xx + nx, yy + ny);

        glTexCoord2d(1, 0);
        glVertex2d(xx - nx, yy - ny);

        glEnd();
    }


    public static Widget newButton() {
        Widget button = new Widget();
        button.size(64, 32);
        button.scale(0.5f);
        button.addComponent(new DrawTexture(texButton));
        button.addComponent(new Tint(Colors.monoBlend(1, 0.6f), Colors.monoBlend(1, 1), true));
        return button;
    }

    public static void drawActionIcon(ResourceLocation icon, boolean glow) {
        int BackSize = 50;
        int IconSize = 27;
        int IconAlign = (BackSize - IconSize) / 2;

        glPushMatrix();
        glTranslated(0, 0, 11);
        glColor4f(1, 1, 1, 1);

        // Draw the background
        RenderUtils.loadTexture(texSkillBack);
        HudUtils.rect(0, 0, BackSize, BackSize);

        // Draw the icon
        RenderUtils.loadTexture(icon);
        HudUtils.rect(IconAlign, IconAlign, IconSize, IconSize);

        // Handle texture binding for glow effect
        glActiveTexture(GL_TEXTURE1);
        int texture1Binding = glGetInteger(GL_TEXTURE_BINDING_2D);

        RenderUtils.loadTexture(texSkillMask);

        // Restore the active texture to 0
        glActiveTexture(GL_TEXTURE0);
        RenderUtils.loadTexture(glow ? texViewOutlineGlow : texViewOutline);
        HudUtils.rect(0, 0, BackSize, BackSize);

        // Restore the texture1 binding
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture1Binding);

        // Restore the active texture to 0 and program
        glActiveTexture(GL_TEXTURE0);

        glPopMatrix();
    }

    public static void send(String channel, Object... args) {
        NetworkMessage.sendToServer(NetDelegateJava.INSTANCE, channel, args);
    }
}