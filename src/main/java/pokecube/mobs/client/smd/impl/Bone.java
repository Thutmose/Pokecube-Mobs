package pokecube.mobs.client.smd.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

/** Bone, has associated Vertices for stretching. */
public class Bone
{
    public Bone                                 copy               = null;
    public String                               name;
    public int                                  ID;
    public Bone                                 parent;
    public Body                                 owner;
    /** transformation matrix for when this bone has not been moved */
    public Matrix4f                             rest;
    public Matrix4f                             restInv;
    /** Transformation matrix for the new position of this bone. */
    public Matrix4f                             transform          = new Matrix4f();
    /** Placeholder to prevent re-newing temporary matrices */
    private final Matrix4f                      dummy1             = new Matrix4f();
    /** Used for any custom animations to store backup data in. */
    public Matrix4f                             custom;
    public Matrix4f                             customInv;
    public ArrayList<Bone>                      children           = new ArrayList<Bone>(0);
    public HashMap<MutableVertex, Float>        verts              = new HashMap<MutableVertex, Float>();
    public HashMap<String, ArrayList<Matrix4f>> animatedTransforms = new HashMap<String, ArrayList<Matrix4f>>();
    public float[]                              currentVals        = new float[6];

    public Bone(Bone b, Bone parent, Body owner)
    {
        this.name = b.name;
        this.ID = b.ID;
        this.owner = owner;
        this.parent = parent;

        Iterator<Map.Entry<MutableVertex, Float>> iterator = b.verts.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<MutableVertex, Float> entry = iterator.next();
            this.verts.put(owner.verts.get(entry.getKey().ID), entry.getValue());
        }
        this.animatedTransforms = new HashMap<String, ArrayList<Matrix4f>>(b.animatedTransforms);
        this.restInv = b.restInv;
        this.rest = b.rest;
        b.copy = this;
    }

    public Bone(String name, int ID, Bone parent, Body owner)
    {
        this.name = name;
        this.ID = ID;
        this.parent = parent;
        this.owner = owner;
    }

    public void addChild(Bone child)
    {
        this.children.add(child);
    }

    public void addVertex(MutableVertex v, float weight)
    {
        if (this.name.equals(
                "blender_implicit")) { throw new UnsupportedOperationException("Cannot add vertex to this part!"); }
        this.verts.put(v, Float.valueOf(weight));
    }

    /** Applies our rest matrix to all child matrices */
    public void applyChildrenToRest()
    {
        for (Bone child : this.children)
        {
            child.applyToRest(this.rest);
        }
    }

    /** modifies the rest matrix by the given matrix.
     * 
     * @param parentMatrix */
    private void applyToRest(Matrix4f parentMatrix)
    {
        this.rest = Matrix4f.mul(parentMatrix, this.rest, null);
        applyChildrenToRest();
    }

    /** Applies our current transform to all associated vertices. */
    public void applyTransform()
    {
        Frame currentFrame = this.owner.getCurrentFrame();
        if (currentFrame != null)
        {
            ArrayList<Matrix4f> precalcArray = this.animatedTransforms.get(currentFrame.owner.name);
            Matrix4f animated = precalcArray.get(currentFrame.ID);
            Matrix4f animatedChange = Matrix4f.mul(animated, this.restInv, this.dummy1);
            this.transform = (this.transform == null ? animatedChange
                    : Matrix4f.mul(this.transform, animatedChange, this.transform));
        }
        for (Map.Entry<MutableVertex, Float> entry : this.verts.entrySet())
        {
            entry.getKey().mutateFromBone(this, entry.getValue().floatValue());
        }
        reset();
    }

    protected Matrix4f getTransform()
    {
        return this.transform == null ? (this.transform = new Matrix4f()) : this.transform;
    }

    public void invertRestMatrix()
    {
        this.restInv = Matrix4f.invert(this.rest, null);
    }

    /** Pre-calculates the needed transform for the given frame.
     * 
     * @param key
     * @param animated */
    public void preloadAnimation(Frame key, Matrix4f animated)
    {
        ArrayList<Matrix4f> precalcArray;
        if (this.animatedTransforms.containsKey(key.owner.name))
        {
            precalcArray = this.animatedTransforms.get(key.owner.name);
        }
        else
        {
            precalcArray = new ArrayList<Matrix4f>();
        }
        Helpers.ensureFits(precalcArray, key.ID);
        precalcArray.set(key.ID, animated);
        this.animatedTransforms.put(key.owner.name, precalcArray);
    }

    /** Prepares the various matrices for the transforms for the current
     * pose. */
    public void prepareTransform()
    {
        dummy1.setIdentity();
        Matrix4f edit = dummy1;
        Matrix4f real;
        Matrix4f realInverted;
        if ((this.owner.parent.hasAnimations()) && (this.owner.currentAnim != null))
        {
            Frame currentFrame = this.owner.currentAnim.frames.get(this.owner.currentAnim.index);
            realInverted = new Matrix4f(currentFrame.transforms.get(this.ID));
            real = new Matrix4f(currentFrame.invertTransforms.get(this.ID));
        }
        else
        {
            realInverted = this.rest;
            real = this.restInv;
        }
        Matrix4f delta = Matrix4f.mul(realInverted, edit, this.dummy1);
        Matrix4f.mul(delta, real, delta);
        this.transform = (this.parent != null ? Matrix4f.mul(this.parent.transform, delta, getTransform()) : delta);
        for (Bone child : this.children)
        {
            child.prepareTransform();
        }
    }

    public void reset()
    {
        this.transform.setIdentity();
    }

    public void setChildren(Bone b, ArrayList<Bone> bones)
    {
        for (int i = 0; i < b.children.size(); i++)
        {
            Bone child = b.children.get(i);
            this.children.add(bones.get(child.ID));
            bones.get(child.ID).parent = this;
        }
    }

    public void setRest(Matrix4f resting)
    {
        this.rest = resting;
    }
}
