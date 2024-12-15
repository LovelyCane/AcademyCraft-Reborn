package cn.lambdalib2.render.primitive;

import cn.lambdalib2.render.Mesh;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SphereMeshFactory {

    public static Mesh create(float radius, int division) {
        Mesh mesh = new Mesh();
        List<Vector3f> vertices = new ArrayList<Vector3f>();

        int vertDiv = division;
        int horizDiv = division * 2;
        for (int i = 0; i <= vertDiv; ++i) {
            float pitch = ((float) i / vertDiv - 0.5f) * 2 * (float) Math.PI;
            float y = MathHelper.sin(pitch);
            float cospitch = MathHelper.cos(pitch);
            for (int j = 0; j < horizDiv; ++j) {
                float yaw = ((float) j / horizDiv) * 2 * (float) Math.PI;
                float x = MathHelper.cos(yaw) * cospitch;
                float z = MathHelper.sin(yaw) * cospitch;

                vertices.add(new Vector3f(x * radius, y * radius, z * radius));
            }
        }

        int[] indices = new int[6 * horizDiv * vertDiv];
        int indicesPos = 0;
        for (int i = 0; i < vertDiv; ++i) {
            int off0 = i * horizDiv, off1 = (1 + i) * horizDiv;
            for (int j = 0; j < horizDiv; ++j) {
                int v0 = off0 + j;
                int v1 = off1 + j;
                int v2 = off1 + j + 1;
                int v3 = off0 + j + 1;

                indices[indicesPos++] = v0;
                indices[indicesPos++] = v1;
                indices[indicesPos++] = v2;
                indices[indicesPos++] = v0;
                indices[indicesPos++] = v2;
                indices[indicesPos++] = v3;
            }
        }

        assert false;

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        return mesh;
    }

}
