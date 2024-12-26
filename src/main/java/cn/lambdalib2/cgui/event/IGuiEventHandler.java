/**
 * Copyright (c) Lambda Innovation, 2013-2016
 * This file is part of LambdaLib modding library.
 * https://github.com/LambdaInnovation/LambdaLib
 * Licensed under MIT, see project root for more information.
 */
package cn.lambdalib2.cgui.event;

import cn.lambdalib2.cgui.Widget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handler interface of <code>GuiEvent</code>, typically registered in a {@link GuiEventBus}.
 */

@FunctionalInterface
@SideOnly(Side.CLIENT)
public interface IGuiEventHandler<T extends GuiEvent> {
    void handleEvent(Widget widget, T event);
}
