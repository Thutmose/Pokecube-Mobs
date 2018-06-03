package pokecube.mobs.client.smd.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.util.ResourceLocation;

/** Animation Object, this contains the various frames of the animations, and
 * applies those to the bones. */
public class Animation
{
    public final Model      owner;
    /** List of frames in this animation. */
    public ArrayList<Frame> frames      = new ArrayList<Frame>();
    /** A list of copies of the bones in owner. They are copied to prevent
     * issues with modifying the original bones. */
    public ArrayList<Bone>  bones       = new ArrayList<Bone>();
    public int              index       = 0;
    public int              lastIndex;
    public int              size;
    public String           name;
    private int             nextFrameID = 0;

    public Animation(Animation anim, Model owner)
    {
        this.owner = owner;
        this.name = anim.name;
        for (Bone b : anim.bones)
        {
            this.bones.add(new Bone(b, b.parent != null ? this.bones.get(b.parent.ID) : null, null));
        }
        for (Frame f : anim.frames)
        {
            this.frames.add(new Frame(f, this));
        }
        this.size = anim.size;
    }

    public Animation(Model owner, String animationName, ResourceLocation resloc) throws Exception
    {
        this.owner = owner;
        this.name = animationName;
        loadAnimation(resloc);
        setBoneChildren();
        apply();
    }

    public int frameCount()
    {
        return this.frames.size();
    }

    public Frame getCurrentFrame()
    {
        if (frames == null || frames.isEmpty() || index < 0 || this.size <= 0) return null;
        return frames.get(index % this.size);
    }

    public int newFrameID()
    {
        int result = this.nextFrameID;
        this.nextFrameID += 1;
        return result;
    }

    /** Loads the animation from the file.
     * 
     * @param resloc
     *            - file with the animation.
     * @throws Exception */
    private void loadAnimation(ResourceLocation resloc) throws Exception
    {
        InputStream inputStream = Helpers.getStream(resloc);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine = null;
        int lineCount = 0;
        try
        {
            while ((currentLine = reader.readLine()) != null)
            {
                lineCount++;
                if (!currentLine.startsWith("version"))
                {
                    if (currentLine.startsWith("nodes"))
                    {
                        lineCount++;
                        while (!(currentLine = reader.readLine()).startsWith("end"))
                        {
                            lineCount++;
                            parseBone(currentLine, lineCount);
                        }
                    }
                    if (currentLine.startsWith("skeleton"))
                    {
                        parseSkeleton(reader, lineCount, resloc);
                    }
                }
            }
        }
        catch (IOException e)
        {
            if (lineCount == -1) { throw new Exception("there was a problem opening the model file : " + resloc, e); }
            throw new Exception("an error occurred reading the SMD file \"" + resloc + "\" on line #" + lineCount, e);
        }
        finally
        {
            reader.close();
        }
    }

    public void nextFrame()
    {
        if (this.index >= this.size - 1)
        {
            this.index = 0;
        }
        else
        {
            this.index += 1;
        }
    }

    private void parseBone(String line, int lineCount)
    {
        String[] params = line.split("\\s+");
        int id = Integer.parseInt(params[0]);
        String boneName = params[1].replaceAll("\"", "");
        int parentID = Integer.parseInt(params[2]);
        Bone parent = parentID >= 0 ? this.bones.get(parentID) : null;
        this.bones.add(id, new Bone(boneName, id, parent, null));
    }

    private void parseSkeleton(BufferedReader reader, int count, ResourceLocation resloc) throws Exception
    {
        int lineCount = count;
        int currentTime = 0;
        lineCount++;
        String currentLine = null;
        try
        {
            while ((currentLine = reader.readLine()) != null)
            {
                lineCount++;
                String[] params = currentLine.split("\\s+");
                if (params[0].equalsIgnoreCase("time"))
                {
                    currentTime = Integer.parseInt(params[1]);
                    this.frames.add(currentTime, new Frame(this));
                }
                else
                {
                    if (currentLine.startsWith("end"))
                    {
                        this.size = this.frames.size();
                        return;
                    }
                    int boneIndex = Integer.parseInt(params[0]);
                    float[] locRots = new float[6];
                    for (int i = 1; i < 7; i++)
                    {
                        locRots[(i - 1)] = Float.parseFloat(params[i]);
                    }
                    Matrix4f animated = Helpers.makeMatrix(locRots[0], -locRots[1], -locRots[2], locRots[3],
                            -locRots[4], -locRots[5]);
                    this.frames.get(currentTime).addTransforms(boneIndex, animated);
                }
            }
        }
        catch (Exception e)
        {
            throw new Exception("an error occurred reading the SMD file \"" + resloc + "\" on line #" + lineCount, e);
        }
    }

    /** Pre-calculates the animation, this primes all of the transform matricies
     * for the various bones.
     * 
     * @param model */
    public void precalculateAnimation(Body model)
    {
        for (int i = 0; i < this.frames.size(); i++)
        {
            model.resetVerts();
            Frame frame = this.frames.get(i);
            for (int j = 0; j < model.bones.size(); j++)
            {
                Bone bone = model.bones.get(j);
                Matrix4f animated = frame.transforms.get(j);
                bone.preloadAnimation(frame, animated);
            }
        }
    }

    /** Sets and applies each frame's transforms.. */
    public void apply()
    {
        int rootID = this.owner.body.root.ID;
        for (int i = 0; i < this.frames.size(); i++)
        {
            Frame frame = this.frames.get(i);
            frame.setTransforms(rootID);
            frame.applyTransforms();
        }
    }

    /** Assigns children/parent relationships for all bones found in the
     * animation. */
    private void setBoneChildren()
    {
        Bone theBone;
        for (int i = 0; i < this.bones.size(); i++)
        {
            theBone = this.bones.get(i);
            for (Bone child : this.bones)
            {
                if (child.parent == theBone)
                {
                    theBone.addChild(child);
                }
            }
        }
    }

    public void setCurrentFrame(int i)
    {
        if (this.lastIndex != i)
        {
            this.index = i;
            this.lastIndex = i;
        }
    }
}