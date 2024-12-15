package cn.lambdalib2.render;

import cn.lambdalib2.render.ShaderScript.InstanceProperty;
import cn.lambdalib2.util.Debug;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class RenderPass {

    private List<DrawCall> drawCalls = new ArrayList<>();
    private List<BatchGroup> batchList = new ArrayList<>();

    private int instanceBuffer = -1;

    public void draw(RenderMaterial material, Mesh mesh) {
        draw(material, mesh, null);
    }

    public void draw(RenderMaterial material, Mesh mesh, InstanceData instanceData) {
        DrawCall drawCall = new DrawCall();
        drawCall.material = material;
        drawCall.mesh = mesh;
        drawCall.instanceData = instanceData;
        drawCalls.add(drawCall);
    }

    public void clear() {
        drawCalls.clear();
    }

    public void dispatch() {
        drawCalls.sort((lhs, rhs) -> compareDict(
             Integer.compare(lhs.material.getDrawOrder(), rhs.material.getDrawOrder()),
             Integer.compare(lhs.material.hashCode(), rhs.material.hashCode()),
             Integer.compare(lhs.mesh.hashCode(), rhs.mesh.hashCode())
        ));

        // Batch draw calls
        {
            int batchingBegin = -1;
            RenderMaterial batchedMaterial = null;
            Mesh batchedMesh = null;

            for (int i = 0; i < drawCalls.size(); ++i) {
                DrawCall drawCall = drawCalls.get(i);
                if (drawCall.material != batchedMaterial || drawCall.mesh != batchedMesh) {
                    if (batchingBegin != -1) {
                        createBatchGroup(batchingBegin, i);
                    }
                    batchingBegin = i;
                    batchedMaterial = drawCall.material;
                    batchedMesh = drawCall.mesh;
                }
            }

            if (batchingBegin != -1)
                createBatchGroup(batchingBegin, drawCalls.size());
        }

        for (BatchGroup batch : batchList) {
            emitDrawCall(batch);
        }

        // cleanup
        drawCalls.clear();
        batchList.clear();
    }

    private void emitDrawCall(BatchGroup batch) {
        RenderMaterial mat = batch.material;
        ShaderScript shader = mat.shader;
        Mesh mesh = batch.mesh;

        // Activate shader program
        glUseProgram(shader.glProgramID);

        // Create vao and setup
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Update material uniform
        mat.updateUniformsOnCurrentProgram();

        // Render states setup
        shader.renderStates.apply();

        // Update mesh buffers (if needed)
        mesh.ensureBuffers();

        // VBO setup
        glBindBuffer(GL_ARRAY_BUFFER, mesh.getVBO());
        List<Mesh.DataLayoutItem> dataLayout = mesh.getDataLayout();

        Map<Mesh.DataType, Integer> layoutMap = new HashMap<>();
        dataLayout.forEach(it -> layoutMap.put(it.dataType, it.offset));

        for (ShaderScript.VertexAttribute va : shader.vertexLayout.values()) {
            Integer offset = layoutMap.get(va.semantic);

            if (offset != null) {
                glEnableVertexAttribArray(va.index);
                glVertexAttribPointer(va.index, va.type.components, GL_FLOAT, false,
                    4 * mesh.getFloatsPerVertex(), offset * 4);
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Instancing setup (if any)
        if (shader.floatsPerInstance > 0) {
            if (instanceBuffer == -1)
                instanceBuffer = glGenBuffers();

            FloatBuffer buf = BufferUploadUtils.requestFloatBuffer(shader.floatsPerInstance * batch.count());
            float[] arr = new float[shader.floatsPerInstance];
            // Populate instance buffer
            for (InstanceData inst : batch.instances) {
                for (InstanceProperty prop : shader.instanceProperties) {
                    int ix = prop.offsetInBuf;

                    Object def = inst.objectMap.get(prop.name);
                    if (def == null)
                        def = prop.value;

                    if (def instanceof Float) {
                        arr[ix] = (float) def;
                    } else if (def instanceof Vector2f) {
                        Vector2f v = ((Vector2f) def);
                        arr[ix] = v.x;
                        arr[ix + 1] = v.y;
                    } else if (def instanceof Vector3f) {
                        Vector3f v = ((Vector3f) def);
                        arr[ix] = v.x;
                        arr[ix + 1] = v.y;
                        arr[ix + 2] = v.z;
                    } else
                        throw new IllegalArgumentException("Invalid instance data " + def + " for " + prop.name);
                }
                buf.put(arr);
            }
            buf.flip();

            // Set GL instance properties
            glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
            glBufferData(GL_ARRAY_BUFFER, buf, GL_DYNAMIC_DRAW);

            // Layout setup
            for (InstanceProperty ip : shader.instanceProperties) {
                Debug.assert2(ip.location != -1, () -> "Invalid instance property " + ip.name);
                glEnableVertexAttribArray(ip.location);
                glVertexAttribPointer(
                    ip.location,
                    ip.type.floatCount,
                    GL_FLOAT,
                    false,
                    4 * shader.floatsPerInstance,
                    (ip.offsetInBuf * 4)
                );
                glVertexAttribDivisor(ip.location, 1);
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        // IBO setup
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());

        // Draw!
        if (shader.floatsPerInstance > 0)
            GL31.glDrawElementsInstanced(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_INT, 0, batch.count());
        else
            glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_INT, 0);

        // Cleanup VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);

        // Cleanup program
        glUseProgram(0);
    }

    private static class DrawCall {
        public RenderMaterial material;
        public Mesh mesh;
        public InstanceData instanceData;
    }

    private static class BatchGroup {
        public RenderMaterial material;
        public Mesh mesh;
        public final List<InstanceData> instances = new ArrayList<>();

        public BatchGroup(RenderMaterial material, Mesh mesh) {
            this.material = material;
            this.mesh = mesh;
        }

        public int count() {
            return instances.size();
        }
    }

    private static int compareDict(int... compareResults) {
        for (int r : compareResults) {
            if (r != 0) return r;
        }
        return 0;
    }

    private void createBatchGroup(int beginIdx, int endIdx) {
        BatchGroup group = new BatchGroup(drawCalls.get(beginIdx).material, drawCalls.get(beginIdx).mesh);
        for (int i = beginIdx; i < endIdx; ++i) {
            group.instances.add(drawCalls.get(i).instanceData);
        }

        batchList.add(group);
    }

}
