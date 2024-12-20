package cn.lambdalib2.render.obj;

import cn.lambdalib2.render.obj.ObjModel.Face;
import cn.lambdalib2.render.obj.ObjModel.Vertex;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;

public class ObjLegacyRender {
    private final ObjModel _model;

    public ObjLegacyRender(ObjModel model) {
        _model = model;
    }

    public void renderAll() {
        for (String k : _model.faces.keySet()) {
            renderPart(k);
        }
    }

    public void renderPart(String part) {
        Collection<Face> list = _model.faces.get(part);
        glBegin(GL_TRIANGLES);

        for (Face f : list) {
            Vertex v0 = _model.vertices.get(f.i0);
            Vertex v1 = _model.vertices.get(f.i1);
            Vertex v2 = _model.vertices.get(f.i2);

            addVertex(v0);
            addVertex(v1);
            addVertex(v2);
        }

        glEnd();
    }

    private void addVertex(Vertex v) {
        glNormal3f(v.normal.x, v.normal.y, v.normal.z);
        glTexCoord2f(v.uv.x, 1 - v.uv.y);
        glVertex3f(v.pos.x, v.pos.y, v.pos.z);
    }
}
