package pokecube.mobs.client.smd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import pokecube.mobs.client.smd.impl.Bone;
import pokecube.mobs.client.smd.impl.Model;
import thut.core.client.render.animation.CapabilityAnimation.IAnimationHolder;
import thut.core.client.render.model.IAnimationChanger;
import thut.core.client.render.model.IExtendedModelPart;
import thut.core.client.render.model.IModel;
import thut.core.client.render.model.IModelCustom;
import thut.core.client.render.model.IModelRenderer;
import thut.core.client.render.model.IPartTexturer;
import thut.core.client.render.model.IRetexturableModel;
import thut.core.client.render.tabula.components.Animation;

public class SMDModel implements IModelCustom, IModel, IRetexturableModel, IFakeExtendedPart
{
    private final HashMap<String, IExtendedModelPart> nullPartsMap = Maps.newHashMap();
    private final HashMap<String, IExtendedModelPart> subPartsMap  = Maps.newHashMap();
    private final Set<String>                         nullHeadSet  = Sets.newHashSet();
    private final Set<String>                         animations   = Sets.newHashSet();
    private final HeadInfo                            info         = new HeadInfo();
    Model                                             wrapped;
    IPartTexturer                                     texturer;
    IAnimationChanger                                 changer;

    public SMDModel()
    {
        nullPartsMap.put(getName(), this);
    }

    public SMDModel(ResourceLocation model)
    {
        this();
        try
        {
            wrapped = new Model(model);
            wrapped.usesMaterials = true;
            animations.addAll(wrapped.anims.keySet());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void applyAnimation(Entity entity, IAnimationHolder animate, IModelRenderer<?> renderer, float partialTicks,
            float limbSwing)
    {
        wrapped.setAnimation(renderer.getAnimation(entity));
    }

    @Override
    public Set<String> getBuiltInAnimations()
    {
        return animations;
    }

    @Override
    public HeadInfo getHeadInfo()
    {
        return info;
    }

    @Override
    public Set<String> getHeadParts()
    {
        return nullHeadSet;
    }

    @Override
    public String getName()
    {
        return "main";
    }

    @Override
    public HashMap<String, IExtendedModelPart> getParts()
    {
        // SMD Renders whole thing at once, so no part rendering.
        return nullPartsMap;
    }

    @Override
    public HashMap<String, IExtendedModelPart> getSubParts()
    {
        return subPartsMap;
    }

    @Override
    public String getType()
    {
        return "smd";
    }

    @Override
    public void preProcessAnimations(Collection<List<Animation>> collection)
    {
        // TODO Bake these animations somehow for the particular SMD model.
    }

    public void render(IModelRenderer<?> renderer)
    {
        if (wrapped != null)
        {
            wrapped.body.setTexturer(texturer);
            wrapped.body.setAnimationChanger(changer);
            // Scaling factor for model.
            GL11.glScaled(0.165, 0.165, 0.165);
            // Makes model face correct way.
            GL11.glRotated(180, 0, 1, 0);

            // only increment frame if a tick has passed.
            if (wrapped.body.currentAnim != null && wrapped.body.currentAnim.frameCount() > 0)
            {
                wrapped.body.currentAnim.setCurrentFrame(info.currentTick % wrapped.body.currentAnim.frameCount());
            }
            // Check head parts for rendering rotations of them.
            for (String s : getHeadParts())
            {
                Bone bone = wrapped.body.getBone(s);
                if (bone != null)
                {
                    // Cap and convert pitch and yaw to radians.
                    float yaw = Math.max(Math.min(info.headYaw, info.yawCapMax), info.yawCapMin);
                    yaw = (float) Math.toRadians(yaw) * info.yawDirection;
                    float pitch = Math.max(Math.min(info.headPitch, info.pitchCapMax), info.pitchCapMin);
                    pitch = (float) Math.toRadians(pitch) * info.pitchDirection;
                    // Rotate Yaw
                    Matrix4f headRot = new Matrix4f();

                    float cosT = (float) Math.cos(pitch);
                    float sinT = (float) Math.sin(pitch);
                    float cosA = (float) Math.cos(yaw);
                    float sinA = (float) Math.sin(yaw);

                    // This matrix is for pitch.
                    Matrix4f rotT = new Matrix4f();
                    // This matrix is for yaw.
                    Matrix4f rotA = new Matrix4f();

                    // Set yaw matrix based on headInfo
                    switch (info.yawAxis)
                    {
                    case 0:
                        rotA.m00 = cosA;
                        rotA.m01 = sinA;
                        rotA.m10 = -sinA;
                        rotA.m11 = cosA;
                        break;
                    case 1:
                        rotA.m00 = cosA;
                        rotA.m02 = sinA;
                        rotA.m20 = -sinA;
                        rotA.m22 = cosA;
                        break;
                    default:
                        rotA.m11 = cosA;
                        rotA.m12 = sinA;
                        rotA.m21 = -sinA;
                        rotA.m22 = cosA;
                    }

                    // Set pitch matrix based on headInfo
                    switch (info.pitchAxis)
                    {
                    case 2:
                        rotT.m11 = cosT;
                        rotT.m12 = sinT;
                        rotT.m21 = -sinT;
                        rotT.m22 = cosT;
                        break;
                    case 0:
                        rotT.m00 = cosT;
                        rotT.m01 = sinT;
                        rotT.m10 = -sinT;
                        rotT.m11 = cosT;
                        break;
                    default:
                        rotT.m00 = cosT;
                        rotT.m02 = sinT;
                        rotT.m20 = -sinT;
                        rotT.m22 = cosT;
                    }
                    // Multiply the two to get total rotation matrix
                    headRot = Matrix4f.mul(rotT, rotA, headRot);
                    // Apply the rotation.
                    bone.applyTransform(headRot);
                }
            }
            if (wrapped.body.currentAnim != null) wrapped.animate();
            wrapped.renderAll();
        }
    }

    @Override
    public void renderAll(IModelRenderer<?> renderer)
    {
        render(renderer);
    }

    @Override
    public void renderAllExcept(IModelRenderer<?> renderer, String... excludedGroupNames)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void renderOnly(IModelRenderer<?> renderer, String... groupNames)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void renderPart(IModelRenderer<?> renderer, String partName)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void setAnimationChanger(IAnimationChanger changer)
    {
        this.changer = changer;
    }

    @Override
    public void setTexturer(IPartTexturer texturer)
    {
        this.texturer = texturer;
    }
}
