package cn.lambdalib2.render.legacy;

import org.lwjgl.opengl.GL20;

/**
 * @author WeAthFolD
 */
public class ShaderSimple extends LegacyShaderProgram {
    private static ShaderSimple instance;
    
    public static ShaderSimple instance() {
        if(instance == null)
            instance = new ShaderSimple();
        return instance;
    }
    
    private ShaderSimple() {
        this.linkShader(getShader("simple.vert"), GL20.GL_VERTEX_SHADER);
        this.linkShader(getShader("simple.frag"), GL20.GL_FRAGMENT_SHADER);
        this.compile();
        
        this.useProgram();
        GL20.glUniform1i(GL20.glGetUniformLocation(this.getProgramID(), "sampler"), 0);
        GL20.glUseProgram(0);
    }
}
