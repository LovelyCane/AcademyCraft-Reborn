/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.cgui;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * A simple wrapper for fast {@link CGui} deploy as GuiScreen.
 * @author WeAthFolD
 */

public class CGuiScreen extends GuiScreen {
    protected CGui gui;
    
    /**
     * Whether the black background should be drawed.
     */
    protected boolean drawBack = true;
    
    public CGuiScreen(CGui _gui) {
        gui = _gui;
    }
    
    public CGuiScreen() {
        this(new CGui());
    }

    @Override
    public void drawScreen(int mx, int my, float w) {
        gui.resize(width, height);
        if(drawBack)
            this.drawDefaultBackground();
        GL11.glPushMatrix(); {
            GL11.glEnable(GL11.GL_BLEND);
            gui.draw(mx, my);
            GL11.glDisable(GL11.GL_BLEND);
        } GL11.glPopMatrix();
    }
    
    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        gui.mouseClicked(mx, my, btn);
    }
    
    @Override
    protected void mouseClickMove(int mx, int my, int btn, long time) {
        gui.mouseClickMove(mx, my, btn, time);
    }
    
    @Override
    public void onGuiClosed() {
        gui.dispose();
    }
    
    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);
        gui.keyTyped(par1, par2);
    }
    
    public CGui getGui() {
        return gui;
    }

}
