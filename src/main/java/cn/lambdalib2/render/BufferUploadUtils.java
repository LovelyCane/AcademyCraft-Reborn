package cn.lambdalib2.render;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class BufferUploadUtils {

    private static ByteBuffer uploadBuffer;

    public static ByteBuffer requestByteBuffer(int size) {
        int capacity = uploadBuffer == null ? 0 : uploadBuffer.capacity();
        if (capacity < size) {
            uploadBuffer = BufferUtils.createByteBuffer(Math.max(size, capacity * 2));
        }

        uploadBuffer.clear().limit(size);
        return uploadBuffer;
    }

    public static FloatBuffer requestFloatBuffer(int size) {
        FloatBuffer buffer = requestByteBuffer(size * 4).asFloatBuffer();
        buffer.clear().limit(size);

        return buffer;
    }

    public static IntBuffer requestIntBuffer(int size) {
        IntBuffer buffer = requestByteBuffer(size * 4).asIntBuffer();
        buffer.clear().limit(size);

        return buffer;
    }

}
