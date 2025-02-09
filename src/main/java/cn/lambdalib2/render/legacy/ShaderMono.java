/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.render.legacy;

import org.lwjgl.opengl.GL20;

/**
 * @author WeAthFolD
 */
public class ShaderMono extends LegacyShaderProgram {
    private static ShaderMono instance;
    
    public static ShaderMono instance() {
        if(instance == null)
            instance = new ShaderMono();
        return instance;
    }
    
    private ShaderMono() {
        this.linkShader(getShader("simple.vert"), GL20.GL_VERTEX_SHADER);
        this.linkShader(getShader("mono.frag"), GL20.GL_FRAGMENT_SHADER);
        this.compile();
        
        this.useProgram();
        GL20.glUniform1i(GL20.glGetUniformLocation(this.getProgramID(), "sampler"), 0);
        GL20.glUseProgram(0);
    }
}
