package pokecube.mobs.client.smd.impl;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import thut.core.client.render.model.TextureCoordinate;
import thut.core.client.render.model.Vertex;

/** A group of vertices, these get moved around by animations on bones, this
 * just holds them */
public class Face
{
    public MutableVertex[]     verts;
    public TextureCoordinate[] uvs;
    public Vertex              normal;

    public Face(Face face, ArrayList<MutableVertex> verts)
    {
        this.verts = new MutableVertex[face.verts.length];
        for (int i = 0; i < this.verts.length; i++)
        {
            this.verts[i] = verts.get(face.verts[i].ID);
        }
        this.uvs = new TextureCoordinate[face.uvs.length];
        System.arraycopy(face.uvs, 0, this.uvs, 0, this.uvs.length);
        if (face.normal != null)
        {
            this.normal = face.normal;
        }
    }

    public Face(MutableVertex[] xyz, TextureCoordinate[] uvs)
    {
        this.verts = xyz;
        this.uvs = uvs;
    }

    /** Add the face for GL rendering
     * 
     * @param smoothShading
     *            - if false, this will render entire face with constant
     *            normal. */
    public void addForRender(boolean smoothShading)
    {
        if (!smoothShading)
        {
            this.normal = calculateNormal();
        }
        for (int i = 0; i < 3; i++)
        {
            GL11.glTexCoord2f(this.uvs[i].u, this.uvs[i].v);
            if (!smoothShading)
            {
                GL11.glNormal3f(this.normal.x, this.normal.y, this.normal.z);
            }
            else
            {
                GL11.glNormal3f(this.verts[i].xn, this.verts[i].yn, this.verts[i].zn);
            }
            GL11.glVertex3d(this.verts[i].x, this.verts[i].y, this.verts[i].z);
        }
    }

    Vector3f a = new Vector3f();
    Vector3f b = new Vector3f();
    Vector3f c = new Vector3f();

    public Vertex calculateNormal()
    {
        a.set(verts[1].x - verts[0].x, verts[1].y - verts[0].y, verts[1].z - verts[0].z);
        b.set(verts[2].x - verts[0].x, verts[2].y - verts[0].y, verts[2].z - verts[0].z);
        c.cross(a, b);
        c.normalize();
        if (normal == null) normal = new Vertex(c.x, c.y, c.z);
        else
        {
            normal.x = c.x;
            normal.y = c.y;
            normal.z = c.z;
        }
        return normal;
    }
}