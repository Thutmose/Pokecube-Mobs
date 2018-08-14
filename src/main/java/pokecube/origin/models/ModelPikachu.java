/**
 *
 */
package pokecube.origin.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import pokecube.core.entity.pokemobs.EntityPokemob;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import thut.api.entity.IMobColourable;

/** @author Manchou */
public class ModelPikachu extends APokemobModel
{
    public ModelPikachu()
    {
        float f = 0.0F;
        headMain = new ModelRenderer(this, 0, 0);
        headMain.addBox(-4F, -3F, -2F, 8, 6, 6, f);
        body = new ModelRenderer(this, 0, 12);
        body.addBox(-5F, -9F, -3F, 8, 12, 6, f);
        footRight = new ModelRenderer(this, 28, 0);
        footRight.addBox(-1F, 0.0F, -1F, 2, 4, 2, f);
        footLeft = new ModelRenderer(this, 28, 0);
        footLeft.addBox(-1F, 0.0F, -1F, 2, 4, 2, f);
        handRight = new ModelRenderer(this, 28, 0);
        handRight.addBox(-1F, 0.0F, -1F, 2, 4, 2, f);
        handLeft = new ModelRenderer(this, 28, 0);
        handLeft.addBox(-1F, 0.0F, -1F, 2, 4, 2, f);
        tail = new ModelRenderer(this, 28, 9);
        tail.addBox(-1F, -2F, -1F, 10, 16, 1, f);
        rightEar = new ModelRenderer(this, 56, 0);
        rightEar.addBox(-4F, -7F, 1F, 2, 7, 2, f);
        leftEar = new ModelRenderer(this, 48, 0);
        leftEar.addBox(2.0F, -7F, 1F, 2, 7, 2, f);
        snout = new ModelRenderer(this, 28, 26);
        snout.addBox(-4F, 0F, -2.5F, 8, 3, 1, f);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch, float scale)
    {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GL11.glPushMatrix();
        if (entity instanceof IMobColourable)
        {
            IMobColourable mob = (IMobColourable) entity;
            int[] cols = mob.getRGBA();
            GL11.glColor4f(cols[0] / 255f, cols[1] / 255f, cols[2] / 255f, cols[3] / 255f);
        }
        IPokemob mob = CapabilityPokemob.getPokemobFor(entity);
        GL11.glTranslated(0, 0.9 + (1 - mob.getSize()) * 0.5, 0);
        GL11.glScaled(0.4 * mob.getSize(), 0.4 * mob.getSize(), 0.4 * mob.getSize());
        headMain.render(scale);
        body.render(scale);
        footRight.render(scale);
        footLeft.render(scale);
        handRight.render(scale);
        handLeft.render(scale);
        rightEar.render(scale);
        leftEar.render(scale);
        snout.renderWithRotation(scale);
        tail.renderWithRotation(scale);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityliving, float limbSwing, float limbSwingAmount,
            float partialTick)
    {
        EntityPokemob entity = (EntityPokemob) entityliving;
        IPokemob mob = CapabilityPokemob.getPokemobFor(entity);

        if (mob.getLogicState(LogicStates.SITTING) || limbSwingAmount < 1e-2 || entity.isRiding())
        {
            float bodyAngle = 0.1F;
            float xOffset = 0;

            body.setRotationPoint(0.0F + xOffset, 21F, 0.0F);
            body.rotateAngleX = bodyAngle;
            tail.setRotationPoint(-5F + xOffset, 21F, 3F);
            footRight.setRotationPoint(-3F + xOffset, 23F, -2F);
            footLeft.setRotationPoint(1F + xOffset, 23F, -2F);
            handRight.setRotationPoint(-4F + xOffset, 15F, -3F);
            handLeft.setRotationPoint(2F + xOffset, 15F, -3F);
            footRight.rotateAngleX = ((float) Math.PI * 3F / 2F);
            footLeft.rotateAngleX = ((float) Math.PI * 3F / 2F);
            footRight.rotateAngleY = 0.5F;
            footLeft.rotateAngleY = -0.5F;
            handRight.rotateAngleX = 5.811947F;
            handLeft.rotateAngleX = 5.811947F;
            headMain.setRotationPoint(-1F + xOffset, 9.5F, -2F);
            snout.setRotationPoint(-1F + xOffset, 9.5F, -2F);
            rightEar.setRotationPoint(-1F + xOffset, 6.5F, -2F);
            leftEar.setRotationPoint(-1F + xOffset, 6.5F, -2F);
        }
        else
        {
            body.setRotationPoint(0.0F, 18F, 2.0F);
            body.rotateAngleX = ((float) Math.PI / 2F);
            tail.setRotationPoint(-5F, 16F, 4F);
            footRight.setRotationPoint(-4F, 20F, 5F);
            footLeft.setRotationPoint(1.5F, 20F, 5F);
            handRight.setRotationPoint(-3.5F, 20F, -4F);
            handLeft.setRotationPoint(1.5F, 20F, -4F);
            walkQuadruped(footRight, footLeft, handRight, handLeft, limbSwing, limbSwingAmount, 0.6662F, 1.4F);
            headMain.setRotationPoint(-1F, 14, -7.5F);
            snout.setRotationPoint(-1F, 14, -7F);
            rightEar.setRotationPoint(-1F, 11, -7F);
            leftEar.setRotationPoint(-1F, 11, -7F);
        }

        float f3 = entity.getInterestedAngle(partialTick) + entity.getShakeAngle(partialTick, 0.0F);
        headMain.rotateAngleZ = f3;
        rightEar.rotateAngleZ = f3;
        leftEar.rotateAngleZ = f3;
        snout.rotateAngleZ = f3;
        body.rotateAngleZ = entity.getShakeAngle(partialTick, -0.16F);
        tail.rotateAngleZ = entity.getShakeAngle(partialTick, -0.2F);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch, float scale, Entity entity)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        headMain.rotateAngleX = headPitch / (180F / (float) Math.PI);
        headMain.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
        rightEar.rotateAngleY = headMain.rotateAngleY;
        rightEar.rotateAngleX = headMain.rotateAngleX;
        leftEar.rotateAngleY = headMain.rotateAngleY;
        leftEar.rotateAngleX = headMain.rotateAngleX;

        if (netHeadYaw >= 0)
        {
            rightEar.rotateAngleZ -= 0.5 * MathHelper.sin(netHeadYaw / 8) + 0.5;
        }
        else
        {
            leftEar.rotateAngleZ += 0.5 * MathHelper.sin(netHeadYaw / 8) + 0.5;
        }

        snout.rotateAngleY = headMain.rotateAngleY;
        snout.rotateAngleX = headMain.rotateAngleX;
        tail.rotateAngleX = 2.8F;
    }

    public ModelRenderer headMain;
    public ModelRenderer body;
    public ModelRenderer footRight;
    public ModelRenderer footLeft;
    public ModelRenderer handRight;
    public ModelRenderer handLeft;
    ModelRenderer        rightEar;
    ModelRenderer        leftEar;
    ModelRenderer        snout;
    ModelRenderer        tail;
}
