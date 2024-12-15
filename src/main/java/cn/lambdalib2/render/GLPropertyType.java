package cn.lambdalib2.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC4;

public enum GLPropertyType {
    Float(1), FloatVec2(2), FloatVec3(3), FloatVec4(4);

    public static GLPropertyType fromGLType(int glType) {
        switch (glType) {
            case GL_FLOAT: return Float;
            case GL_FLOAT_VEC2: return FloatVec2;
            case GL_FLOAT_VEC3: return FloatVec3;
            case GL_FLOAT_VEC4: return FloatVec4;
            default: throw new RuntimeException("Unsupported GL type " + glType);
        }
    }

    public final int components;

    GLPropertyType(int _components) {
        components = _components;
    }
}
