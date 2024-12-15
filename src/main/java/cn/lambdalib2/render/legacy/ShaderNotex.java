package cn.lambdalib2.render.legacy;

import org.lwjgl.opengl.GL20;

/**
 * @author WeAthFolD
 */
public class ShaderNotex extends LegacyShaderProgram {
    
    private static ShaderNotex instance;
    
    public static ShaderNotex instance() {
        if(instance == null) {
            instance = new ShaderNotex();
        }
        return instance;
    }
    
    private ShaderNotex() {
        this.linkShader(getShader("simple.vert"), GL20.GL_VERTEX_SHADER);
        this.linkShader(getShader("notex.frag"), GL20.GL_FRAGMENT_SHADER);
        this.compile();
    }
    
}
