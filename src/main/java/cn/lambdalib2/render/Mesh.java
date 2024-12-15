package cn.lambdalib2.render;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL15.*;

public class Mesh {

    public static final int VertexCountLimit = 65536;

    enum DataType {
        Position, Color, UV1, UV2, UV3, UV4
    }

    class DataLayoutItem {
        DataType dataType;
        int offset;

        private DataLayoutItem(DataType dataType, int offset) {
            this.dataType = dataType;
            this.offset = offset;
        }
    }

    // Vertex buffer layout: vertex [color] [uv1] [uv2] [uv3] [uv4]
    // max width: 3 + 4 + 4 * 4 = 23

    private final List<Vector3f> vertices = new ArrayList<>();

    private final List<Vector4f> colors = new ArrayList<>();

    private final List<?> uvs[] = new List[] {
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
    };


    private int[] indices = new int[0];

    private boolean dirty = true;

    private boolean dynamic = false;

    private int vbo, ibo;

    private final List<DataLayoutItem> dataLayout = new ArrayList<>();

    private int floatsPerVertex;

    public void setVertices(List<Vector3f> vertices) {
        this.vertices.clear();
        this.vertices.addAll(vertices);
    }

    public void setVertices(Vector3f[] vertices) {
        setVertices(Arrays.asList(vertices));
    }

    public void setColors(List<Vector4f> colors) {
        this.colors.clear();
        this.colors.addAll(colors);
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public void setUVsVec2(int channel, List<Vector2f> uv) {
        uvs[channel] = new ArrayList<>(uv);
        markDirty();
    }

    public void setUVsVec2(int channel, Vector2f[] uv) {
        setUVsVec2(channel, Arrays.asList(uv));
    }

    public void setUVsVec3(int channel, List<Vector3f> uv) {
        uvs[channel] = new ArrayList<>(uv);
        markDirty();
    }

    public void setUVsVec3(int channel, Vector3f[] uv) {
        setUVsVec3(channel, Arrays.asList(uv));
    }

    public void setUVsVec4(int channel, List<Vector4f> uv) {
        uvs[channel] = new ArrayList<>(uv);
        markDirty();
    }

    public void setUVsVec4(int channel, Vector4f[] uv) {
        setUVsVec4(channel, Arrays.asList(uv));
    }

    public void makeDynamic() {
        dynamic = true;
        markDirty();
    }

    public int getIndicesCount() {
        return indices.length;
    }

    int getVBO() {
        return vbo;
    }

    int getIBO() {
        return ibo;
    }

    /**
     * Internally, ensureBuffers() must be called BEFORE mesh's vbo and ibo
     *  are used. It will check dirtiness and upload the data to the buffers.
     */
    void ensureBuffers() {
        if (!dirty) return;
        dirty = false;

        if (!glIsBuffer(vbo)) {
            vbo = glGenBuffers();
        }
        if (!glIsBuffer(ibo)) {
            ibo = glGenBuffers();
        }

        updateVertexLayout();

        int usage = dynamic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW;

        FloatBuffer vboUpload = BufferUploadUtils.requestFloatBuffer(vertices.size() * floatsPerVertex);
        vboUpload.clear();

        for (int i = 0; i < vertices.size(); ++i) {
            for (DataLayoutItem layout : dataLayout) {
                List<?> dataList = getDataList(layout.dataType);
                Object element = dataList.get(i);
                if (element instanceof Float) {
                    vboUpload.put((float) element);

                } else if (element instanceof Vector2f) {
                    Vector2f v = (Vector2f) element;
                    vboUpload.put(v.x).put(v.y);

                } else if (element instanceof Vector3f) {
                    Vector3f v = (Vector3f) element;
                    vboUpload.put(v.x).put(v.y).put(v.z);

                } else if (element instanceof Vector4f) {
                    Vector4f v = (Vector4f) element;
                    vboUpload.put(v.x).put(v.y).put(v.z).put(v.w);

                } else {
                    throw new RuntimeException("Unreachable");
                }
            }
        }

        vboUpload.flip();

        int lastArrayBufferBinding = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vboUpload, usage);
        glBindBuffer(GL_ARRAY_BUFFER, lastArrayBufferBinding);


        IntBuffer iboUpload = BufferUploadUtils.requestIntBuffer(indices.length);
        iboUpload.put(indices);
        iboUpload.flip();

        int lastEABinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, iboUpload, usage);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, lastEABinding);
    }

    int getFloatsPerVertex() {
        return floatsPerVertex;
    }

    List<DataLayoutItem> getDataLayout() {
        return this.dataLayout;
    }

    private void updateVertexLayout() {
        dataLayout.clear();

        int offset = 0;

        dataLayout.add(new DataLayoutItem(DataType.Position, offset));
        offset += 3;

        if (!colors.isEmpty()) {
            verifyDataLength(colors, "Color");
            dataLayout.add(new DataLayoutItem(DataType.Color, offset));
            offset += 4;
        }

        if (!uvs[0].isEmpty()) {
            verifyDataLength(uvs[0], "UV1");
            dataLayout.add(new DataLayoutItem(DataType.UV1, offset));
            offset += getFloatWidth(uvs[0]);
        }
        if (!uvs[1].isEmpty()) {
            verifyDataLength(uvs[1], "UV2");
            dataLayout.add(new DataLayoutItem(DataType.UV2, offset));
            offset += getFloatWidth(uvs[1]);
        }
        if (!uvs[2].isEmpty()) {
            verifyDataLength(uvs[2], "UV3");
            dataLayout.add(new DataLayoutItem(DataType.UV3, offset));
            offset += getFloatWidth(uvs[2]);
        }
        if (!uvs[3].isEmpty()) {
            verifyDataLength(uvs[3], "UV4");
            dataLayout.add(new DataLayoutItem(DataType.UV4, offset));
            offset += getFloatWidth(uvs[3]);
        }

        floatsPerVertex = offset;
    }

    private void verifyDataLength(List<?> list, String dataName) {
        if (list.size() != vertices.size()) {
            throw new RuntimeException(dataName + "'s length differs than vertices.");
        }
    }

    private void markDirty() {
        dirty = true;
    }

    private int getFloatWidth(List<?> vecList) {
        Object o = vecList.get(0);
        if (o instanceof Vector2f)
            return 2;
        if (o instanceof Vector3f)
            return 3;
        if (o instanceof Vector4f)
            return 4;
        throw new RuntimeException("Should never reach here");
    }

    private List<?> getDataList(DataType type) {
        switch (type) {
            case Position: return vertices;
            case Color: return colors;
            case UV1: return uvs[0];
            case UV2: return uvs[1];
            case UV3: return uvs[2];
            case UV4: return uvs[3];
            default: throw new RuntimeException("Unreachable code");
        }
    }

}
