package pokecube.mobs.client.smd.impl;

import java.util.ArrayList;

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
        if (!smoothShading && this.normal == null)
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

    public Vertex calculateNormal()
    {
        // TODO calculate the real normal for the face.
        return new Vertex(0, 0, 0);
    }
}