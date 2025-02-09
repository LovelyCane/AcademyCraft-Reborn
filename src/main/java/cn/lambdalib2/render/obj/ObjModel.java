package cn.lambdalib2.render.obj;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ObjModel {
    public final List<Vertex> vertices = new ArrayList<>();
    public final Multimap<String, Face> faces = HashMultimap.create();

    public static class Vertex {

        public final Vector3f pos;
        public final Vector2f uv;
        public final Vector3f tangent;
        public final Vector3f normal;

        public Vertex(Vector3f pos, Vector2f uv) {
            this.pos = pos;
            this.uv = uv;
            this.tangent = new Vector3f();
            this.normal = new Vector3f();
        }

    }

    public static class Face {
        public final int i0, i1, i2;
        public final Vector3f tangent = new Vector3f();
        public final Vector3f normal = new Vector3f();

        public Face(int i0, int i1, int i2) {
            this.i0 = i0;
            this.i1 = i1;
            this.i2 = i2;
        }

        @Override
        public String toString() {
            return "Face{" + "i0=" + i0 + ", i1=" + i1 + ", i2=" + i2 + '}';
        }
    }
}
