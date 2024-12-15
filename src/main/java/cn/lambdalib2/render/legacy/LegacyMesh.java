package cn.lambdalib2.render.legacy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeAthFolD
 */
@Deprecated // Too bad abstraction and efficiency. Work harder!
@SideOnly(Side.CLIENT)
public class LegacyMesh {

    protected double[][] vertices;
    
    protected double[][] uvs;
    
    protected float[][] normals;
    
    protected int[] triangles;
    
    protected List<LegacyMesh> sub = new ArrayList<LegacyMesh>();
    
    /**
     * Determine whether this mesh needs to be buffered(Compiled into a display list).
     */
    protected boolean doesBuffer = false;
    
    int listID = -1;
    
    public LegacyMesh() {}
    
    public int addMesh(LegacyMesh m) {
        sub.add(m);
        return sub.size() - 1;
    }
    
    public void removeMesh(LegacyMesh m) {
        sub.remove(m);
    }
    
    public LegacyMesh getSubMesh(int id) {
        return sub.get(id);
    }
    
    public LegacyMesh setUVs(double[][] uvs) {
        if(vertices == null || vertices.length != uvs.length) {
            throw new IllegalStateException("UVs size must be equal to vert size");
        }
        this.uvs = uvs;
        return this;
    }
    
    public LegacyMesh setVertices(double[][] vertices) {
        this.vertices = vertices;
        if(uvs != null && uvs.length != vertices.length) uvs = null;
        if(normals != null && normals.length != vertices.length) normals = null;
        //Dont check triangle.
        return this;
    }
    
    public LegacyMesh setVertex(int ind, double[] vert) {
        vertices[ind] = vert;
        return this;
    }
    
    public LegacyMesh setUV(int ind, double[] uv) {
        uvs[ind] = uv;
        return this;
    }
    
    public LegacyMesh setTriangles(int[] triangles) {
        this.triangles = triangles;
        return this;
    }
    
    public LegacyMesh setAllNormals(float[] normal) {
        normals = new float[vertices.length][];
        for(int i = 0; i < vertices.length; ++i) {
            normals[i] = normal;
        }
        return this;
    }
    
    public LegacyMesh setNormals(float[][] normals) {
        if(vertices == null || vertices.length != uvs.length) {
            throw new IllegalStateException("Normals size must be equal to vert size");
        }
        this.normals = normals;
        return this;
    }
    
    /**
     * Decompose to triangles.
     */
    public LegacyMesh setQuads(int[] quads) {
        if(quads.length % 4 != 0) {
            System.err.println("You should specify quads by a list of length of multiply of 4.");
        }
        int[] result = new int[(quads.length / 4) * 6];
        int j = 0;
        for(int i = 0; i + 3 < quads.length; i += 4, j += 6) {
            result[j]       = quads[i];
            result[j + 1] = quads[i + 1];
            result[j + 2] = quads[i + 2];
            
            result[j + 3] = quads[i];
            result[j + 4] = quads[i + 2];
            result[j + 5] = quads[i + 3];
        }
        setTriangles(result);
        return this;
    }
    
    public LegacyMesh setQuads(Integer[] quads) {
        if(quads.length % 4 != 0) {
            System.err.println("You should specify quads by a list of length of multiply of 4.");
        }
        int[] result = new int[(quads.length / 4) * 6];
        int j = 0;
        for(int i = 0; i + 3 < quads.length; i += 4, j += 6) {
            result[j]       = quads[i];
            result[j + 1] = quads[i + 1];
            result[j + 2] = quads[i + 2];
            
            result[j + 3] = quads[i];
            result[j + 4] = quads[i + 2];
            result[j + 5] = quads[i + 3];
        }
        setTriangles(result);
        return this;
    }
    
    private void redraw(LegacyMaterial mat, boolean execute) {
        if(doesBuffer) {
            if(listID == -1) {
                listID = GL11.glGenLists(1);
                GL11.glNewList(listID, execute ? GL11.GL_COMPILE_AND_EXECUTE : GL11.GL_COMPILE);
            }
        }
        
        mat.onRenderStage(RenderStage.START);

        mat.onRenderStage(RenderStage.BEFORE_TESSELLATE);
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_TRIANGLES);
        mat.onRenderStage(RenderStage.START_TESSELLATE);

        redrawWithinBatch(mat);
        
        t.draw();
        
        mat.onRenderStage(RenderStage.END);
        
        for(LegacyMesh m : this.sub) {
            m.draw(mat);
        }
        
        if(doesBuffer) {
            GL11.glEndList();
        }
    }
    
    public void redraw(LegacyMaterial mat) {
        redraw(mat, true);
    }

    public void redrawWithinBatch(LegacyMaterial mat) {
        Tessellator t = Tessellator.instance;
        if(uvs != null) {
            if(triangles != null) {
                for(int i : triangles) {
                    double[] vert = vertices[i];
                    double[] uv = uvs[i];
                    if(normals != null) {
                        t.setNormal(normals[i][0], normals[i][1], normals[i][2]);
                    }
                    t.addVertexWithUV(vert[0], vert[1], vert[2], uv[0], uv[1]);
                }
            }
        } else {
            if(triangles != null) {
                for(int i : triangles) {
                    double[] vert = vertices[i];
                    if(normals != null) {
                        t.setNormal(normals[i][0], normals[i][1], normals[i][2]);
                    }
                    t.addVertex(vert[0], vert[1], vert[2]);
                }
            }
        }
    }
    
    public void draw(LegacyMaterial mat) {
        if(doesBuffer) {
            if(listID == -1) {
                redraw(mat, true);
            } else
                GL11.glCallList(listID);
        } else {
            redraw(mat, true);
        }
    }
    
    
}
