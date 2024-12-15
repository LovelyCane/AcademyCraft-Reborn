/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.render.legacy;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glNormal3d;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * External mesh drawer using shader.
 * @author WeAthFolD
 */
public class GLSLMesh extends LegacyMesh {
    
    public GLSLMesh() {}
    
    public void draw(LegacyShaderProgram program) {
        draw(program.getProgramID());
    }
    
    /**
     * Draw the mesh using the specified shader program.
     */
    public void draw(int programID) {
        glUseProgram(programID);
        glBegin(GL_TRIANGLES);
        for(int j = 0; j < triangles.length; ++j) {
            int i = triangles[j];
            if(uvs != null) {
                glTexCoord2d(uvs[i][0], uvs[i][1]);
            } else {
                glTexCoord2d(0, 0);
            }
            if(normals != null) {
                glNormal3d(normals[i][0], normals[i][1], normals[i][2]);
            } else {
                glNormal3d(0, 0, 0);
            }
            glVertex3d(vertices[i][0], vertices[i][1], vertices[i][2]);
        }
        glEnd();
        glUseProgram(0);
    }
    
    @Override
    public void draw(LegacyMaterial mat) {
        //System.out.println("Current program: " + GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
        mat.onRenderStage(RenderStage.START);
        mat.onRenderStage(RenderStage.TRANSFORM);
        mat.onRenderStage(RenderStage.BEFORE_TESSELLATE);
        draw(GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
        mat.onRenderStage(RenderStage.END);
    }
    
    @Override
    public void redraw(LegacyMaterial mat) {
        throw new NotImplementedException("GLSLMesh doesn't handle tess drawing routine");
    }
    
}
