package cn.lambdalib2.util;

import java.lang.reflect.Field;

import cn.lambdalib2.LambdaLib2;
import cn.lambdalib2.render.legacy.Tessellator;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex3d;

/**
 * Utils for legacy GL rendering.
 * Note that you should use modern GL as much as possible and avoid this.
 * @author WeAthFolD
 */
@Deprecated
public class RenderUtils {
    
//    public static ResourceLocation src_glint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private static int textureState = -1;
    
    //-----------------Quick aliases-----------------------------
    
    /**
     * Stores the current texture state. stack depth: 1
     */
    public static void pushTextureState() {
        if(textureState != -1) {
            throw new RuntimeException("RenderUtils:Texture State Overflow");
        }
        textureState = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
    }
    
    /**
     * Restores the stored texture state. stack depth: 1
     */
    public static void popTextureState() {
        if(textureState == -1) {
            throw new RuntimeException("RenderUtils:Texture State Underflow");
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureState);
        textureState = -1;
    }

    public static void glVertexAndUV(Vec3d vertex, double u, double v) {
        GL11.glTexCoord2d(u, v);
        glVertex3d(vertex.x, vertex.y, vertex.z);
    }

    public static void glVertexAndUV(double x, double y, double z, double u, double v) {
        glTexCoord2d(u, v);
        glVertex3d(x, y, z);
    }
    
    public static void glVertex(Vec3d vertex) {
        glVertex3d(vertex.x, vertex.y, vertex.z);
    }
    
    public static void glTranslate(Vec3d v) {
        GL11.glTranslated(v.x, v.y, v.z);
    }
    
    public static void glScale(Vec3d v) {
        GL11.glScaled(v.x, v.y, v.z);
    }
    
    public static void glRotate(double angle, Vec3d axis) {
        GL11.glRotated(angle, axis.x, axis.y, axis.z);
    }
    
    public static void loadTexture(ResourceLocation src) {
        Minecraft.getMinecraft().renderEngine.bindTexture(src);
    }

    public static void drawEquippedItem(ItemStack stackToRender, double width) {
        Debug.TODO();
//        IIcon icon = stackToRender.getIconIndex();
//
//        Minecraft mc = Minecraft.getMinecraft();
//        mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(stackToRender.getItemSpriteNumber()));
//        ResourceLocation tex = mc.renderEngine.getResourceLocation(stackToRender.getItemSpriteNumber());
//
//        drawEquippedItem(width, tex, tex, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), false);
    }
    
    public static void drawEquippedItem(double width, ResourceLocation texture) {
        drawEquippedItem(width, texture, texture);
    }
    
    public static void drawEquippedItemOverlay(double width, ResourceLocation texture) {
        drawEquippedItem(width, texture, texture, 0, 0, 1, 1, true);
    }
    
    public static void drawEquippedItem(double width, ResourceLocation front, ResourceLocation back) {
        drawEquippedItem(width, front, back, 0, 0, 1, 1, false);
    }
    
    private static void drawEquippedItem(double w, ResourceLocation front, ResourceLocation back, 
            double u1, double v1, double u2, double v2, boolean faceOnly) {
        Tessellator t = Tessellator.instance;
        Vec3d a1 = new Vec3d(0, 0, w),
            a2 = new Vec3d(1, 0, w),
            a3 = new Vec3d(1, 1, w),
            a4 = new Vec3d(0, 1, w),
            a5 = new Vec3d(0, 0, -w),
            a6 = new Vec3d(1, 0, -w),
            a7 = new Vec3d(1, 1, -w),
            a8 = new Vec3d(0, 1, -w);

        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();

        RenderUtils.loadTexture(back);
        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, 1.0F);
        addVertex(a1, u2, v2);
        addVertex(a2, u1, v2);
        addVertex(a3, u1, v1);
        addVertex(a4, u2, v1);
        t.draw();

        RenderUtils.loadTexture(front);
        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, -1.0F);
        addVertex(a8, u2, v1);
        addVertex(a7, u1, v1);
        addVertex(a6, u1, v2);
        addVertex(a5, u2, v2);
        t.draw();

        int var7;
        float var8;
        double var9;
        float var10;

        /*
         * Gets the width/16 of the currently bound texture, used to fix the
         * side rendering issues on textures != 16
         */
        int tileSize = 32;
        float tx = 1.0f / (32 * tileSize);
        float tz = 1.0f / tileSize;

        if(!faceOnly) {
            t.startDrawingQuads();
            t.setNormal(-1.0F, 0.0F, 0.0F);
            for (var7 = 0; var7 < tileSize; ++var7) {
                var8 = (float) var7 / tileSize;
                var9 = u2 - (u2 - u1) * var8 - tx;
                var10 = 1.0F * var8;
                t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
                t.addVertexWithUV(var10, 0.0D, w, var9, v2);
                t.addVertexWithUV(var10, 1.0D, w, var9, v1);
                t.addVertexWithUV(var10, 1.0D, -w, var9, v1);

                t.addVertexWithUV(var10, 1.0D, w, var9, v1);
                t.addVertexWithUV(var10, 0.0D, w, var9, v2);
                t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
                t.addVertexWithUV(var10, 1.0D, -w, var9, v1);
            }
            t.draw();
        }

        GL11.glPopMatrix();
    }

    private static void addVertex(Vec3d vec, double u, double v) {
        Tessellator t = Tessellator.instance;
        t.addVertexWithUV(vec.x, vec.y, vec.z, u, v);
    }
    
    public static void renderOverlayEquip(ResourceLocation src) {
        Debug.TODO();
//        //Setup
//        GL11.glDepthFunc(GL11.GL_EQUAL);
//        GL11.glDisable(GL11.GL_LIGHTING);
//        loadTexture(src);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
//        float f7 = 0.76F;
//        GL11.glMatrixMode(GL11.GL_TEXTURE);
//        //Push texture mat
//        GL11.glPushMatrix();
//        float f8 = 0.125F;
//        GL11.glScalef(f8, f8, f8);
//        float f9 = GameTimer.getAbsTime() % 3000L / 3000.0F * 8.0F;
//        GL11.glTranslatef(f9, 0.0F, 0.0F); //xOffset loops between 0.0 and 8.0
//        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//        ItemRenderer.renderItemIn2D(t, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//        GL11.glPopMatrix();
//
//        //Second pass
//        GL11.glPushMatrix();
//        GL11.glScalef(f8, f8, f8);
//        f9 = GameTimer.getAbsTime() % 4873L / 4873.0F * 8.0F; //Loop between 0 and 8, longer loop
//        GL11.glTranslatef(-f9, 0.0F, 0.0F); //Still xOffset
//        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F); //However, different rotation!
//        ItemRenderer.renderItemIn2D(t, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//        GL11.glPopMatrix();
//        //Pop texture mat
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }
    
//    public static void renderEnchantGlintEquip() {
//        GL11.glColor3f(0.301F, 0.78F, 1.0F);
//        renderOverlayEquip(src_glint);
//    }
    
    public static void renderOverlayInv(ResourceLocation src) {
        Debug.TODO();
//        GL11.glDepthFunc(GL11.GL_EQUAL);
//        GL11.glDisable(GL11.GL_LIGHTING);
//        loadTexture(src);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
//        float f7 = 0.76F;
//        //GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
//        GL11.glMatrixMode(GL11.GL_TEXTURE);
//        GL11.glPushMatrix();
//        float f8 = 0.125F;
//        GL11.glScalef(f8, f8, f8);
//        float f9 = GameTimer.getAbsTime() % 3000L / 3000.0F * 8.0F;
//        GL11.glTranslatef(f9, 0.0F, 0.0F);
//        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//        t.startDrawingQuads();
//        t.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
//        t.addVertexWithUV(0.0, 16.0, 0.0, 0.0, 1.0);
//        t.addVertexWithUV(16.0, 16.0, 0.0, 1.0, 1.0);
//        t.addVertexWithUV(16.0, 0.0, 0.0, 1.0, 0.0);
//        t.draw();
//        GL11.glPopMatrix();
//        GL11.glPushMatrix();
//        GL11.glScalef(f8, f8, f8);
//        f9 = GameTimer.getAbsTime() % 4873L / 4873.0F * 8.0F;
//        GL11.glTranslatef(-f9, 0.0F, 0.0F);
//        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//        t.startDrawingQuads();
//        t.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
//        t.addVertexWithUV(0.0, 16.0, 0.0, 0.0, 1.0);
//        t.addVertexWithUV(16.0, 16.0, 0.0, 1.0, 1.0);
//        t.addVertexWithUV(16.0, 0.0, 0.0, 1.0, 0.0);
//        t.draw();
//        GL11.glPopMatrix();
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    public static void addVertexLegacy(Vec3d vertex, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(vertex.x, vertex.y, vertex.z);
    }
    
    /**
     * 直接在物品栏渲染物品icon。确认你已经绑定好贴图。
     * @param item
     */
    public static void renderItemInventory(ItemStack item) {
        Debug.TODO();
//        IIcon icon = item.getIconIndex();
//        renderItemInventory(icon);
    }

    /**
     * 直接在物品栏渲染物品icon。确认你已经绑定好贴图。
     */
//    public static void renderItemInventory(IIcon icon) {
//        Debug.TODO();
////        if(icon != null) {
////            t.startDrawingQuads();
////            t.addVertexWithUV(0.0, 0.0, 0.0, icon.getMinU(), icon.getMinV());
////            t.addVertexWithUV(0.0, 16.0, 0.0, icon.getMinU(), icon.getMaxV());
////            t.addVertexWithUV(16.0, 16.0, 0.0, icon.getMaxU(), icon.getMaxV());
////            t.addVertexWithUV(16.0, 0.0, 0.0, icon.getMaxU(), icon.getMinV());
////            t.draw();
////        }
//    }

    //--- SMC Support
    static final String _shadersClassName = "shadersmodcore.client.Shaders";
    static boolean smcSupportInit = false;
    static boolean smcPresent = false;
    static Field fIsShadowPass, fisRenderingDfb, fIsRenderingSky;
    
    /**
     * Judge whether the current rendering context is in shadow pass.
     * @return If in vanilla Minecraft: always false; If ShaderMod is installed: as mentioned above.
     */
    public static boolean isInShadowPass() {
        if(!smcSupportInit) {
            initSMCSupport();
        }
        
        if(smcPresent) {
            try {
                return fIsShadowPass.getBoolean(null);
            } catch(Exception e) {
                Debug.error("Exception in isInShadowPass", e);
            }
        }
        return false;
    }
    
    private static void initSMCSupport() {
        try {
            Class shadersClass = Class.forName(_shadersClassName);
            fIsShadowPass = shadersClass.getField("isShadowPass");
            smcPresent = true;
            Debug.log("LambdaLib SMC support successfully initialized.");
        } catch(Exception e) {
            Debug.error("LambdaLib SMC support isn't initialized.");
            smcPresent = false;
        }
        
        smcSupportInit = true;
    }

    public static void drawBlackout() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(1, 0, 1, 0);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
        GL11.glColor4d(0, 0, 0, 0.7);
        GL11.glTranslated(0, 0, 0);
        HudUtils.colorRect(0, 0, 1, 1);
        
        GL11.glPopMatrix();
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(1, 1, 1, 1);
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    
}
