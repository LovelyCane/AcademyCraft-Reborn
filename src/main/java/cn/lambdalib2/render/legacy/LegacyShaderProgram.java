package cn.lambdalib2.render.legacy;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ResourceUtils;
import com.google.common.base.Throwables;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * A simple GL Shader Program wrapper.
 * @author WeAthFolD
 */
@SideOnly(Side.CLIENT)
public class LegacyShaderProgram {
    
    private boolean compiled = false;
    private boolean valid = false;
    private int programID;
    private List<Integer> attachedShaders = new ArrayList<>();
    
    public LegacyShaderProgram() {
        programID = glCreateProgram();
    }
    
    public void linkShader(ResourceLocation location, int type) {
        if (!checkCapability())
            return;

        try {
            boolean loaded;
            String str = IOUtils.toString(ResourceUtils.getResourceStream(location));
            int shaderID = glCreateShader(type);
            glShaderSource(shaderID, str);
            glCompileShader(shaderID);

            int successful = glGetShaderi(shaderID, GL_COMPILE_STATUS);
            if(successful == GL_FALSE) {
                String log = glGetShaderInfoLog(shaderID, glGetShaderi(shaderID, GL_INFO_LOG_LENGTH));
                Debug.error("Error when linking shader '" + location + "'. code: " + successful + ", Error string: \n" + log);
                loaded = false;
            } else {
                loaded = true;
            }

            if (loaded) {
                attachedShaders.add(shaderID);
                glAttachShader(programID, shaderID);
            }
        } catch (IOException e) {
            Debug.error("Didn't find shader " + location, e);
            Throwables.propagate(e);
        }
    }
    
    public int getProgramID() {
        return programID;
    }
    
    public void useProgram() {
        if(compiled && valid) {
            glUseProgram(programID);
        } else if (!compiled) {
            Debug.error("Trying to use a uncompiled program");
            throw new RuntimeException();
        } // not valid, ignore the shader usage
    }
    
    public int getUniformLocation(String name) {
        return glGetUniformLocation(getProgramID(), name);
    }
    
    public void compile() {
        if (!checkCapability()) {
            compiled = true;
            return;
        }

        if(compiled) {
            Debug.error("Trying to compile shader " + this + " twice.");
            throw new RuntimeException();
        }
        
        glLinkProgram(programID);
        
        for(Integer i : attachedShaders)
            glDetachShader(programID, i);
        attachedShaders = null;
        
        int status = glGetProgrami(programID, GL_LINK_STATUS);
        if(status == GL_FALSE) {
            String log = glGetProgramInfoLog(programID, glGetProgrami(programID, GL_INFO_LOG_LENGTH));
            Debug.error("Error when linking program #" + programID + ". Error code: " + status + ", Error string: ");
            Debug.error(log);
            valid = false;
        } else {
            valid = true;
        }
        
        compiled = true;
    }

    public boolean isValid() {
        return valid;
    }

    private boolean checkCapability() {
        String versionShort = GL11.glGetString(GL11.GL_VERSION).trim().substring(0, 3);
        return "2.1".compareTo(versionShort) <= 0;
    }
    
    /**
     * Get the src of a shader in lambdalib namespace.
     */
    public static ResourceLocation getShader(String name) {
        return new ResourceLocation("lambdalib2:legacy_shader/" + name);
    }
    
}
