package walksy.shield.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.main.ShieldFixMod;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {


    @Shadow
    @Final
    public ModelPart hat;

    @Shadow
    @Final
    public ModelPart leftLeg;

    @Shadow
    @Final
    public ModelPart rightLeg;

    @Shadow
    public float leaningPitch;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    protected abstract float lerpAngle(float angleOne, float angleTwo, float magnitude);

    @Shadow
    protected abstract float method_2807(float f);

    @Shadow
    protected abstract Arm getPreferredArm(T entity);

    @Shadow
    public BipedEntityModel.ArmPose leftArmPose;

    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart body;

    @Shadow
    protected abstract void animateArms(T entity, float animationProgress);

    @Shadow
    public boolean sneaking;

    @Shadow
    protected abstract void positionRightArm(T entity);

    @Shadow
    protected abstract void positionLeftArm(T entity);

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    private void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci)
    {
        ci.cancel();
        boolean bl = livingEntity.getRoll() > 4;
        boolean bl2 = livingEntity.isInSwimmingPose();
        this.head.yaw = i * 0.017453292F;
        if (bl) {
            this.head.pitch = -0.7853982F;
        } else if (this.leaningPitch > 0.0F) {
            if (bl2) {
                this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982F);
            } else {
                this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, j * 0.017453292F);
            }
        } else {
            this.head.pitch = j * 0.017453292F;
        }

        this.body.yaw = 0.0F;
        this.rightArm.pivotZ = 0.0F;
        this.rightArm.pivotX = -5.0F;
        this.leftArm.pivotZ = 0.0F;
        this.leftArm.pivotX = 5.0F;
        float k = 1.0F;
        if (bl) {
            k = (float)livingEntity.getVelocity().lengthSquared();
            k /= 0.2F;
            k *= k * k;
        }

        if (k < 1.0F) {
            k = 1.0F;
        }

        this.rightArm.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F / k;
        this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
        this.rightArm.roll = 0.0F;
        this.leftArm.roll = 0.0F;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g / k;
        this.rightLeg.yaw = 0.005F;
        this.leftLeg.yaw = -0.005F;
        this.rightLeg.roll = 0.005F;
        this.leftLeg.roll = -0.005F;
        ModelPart var10000;
        if (this.riding) {
            var10000 = this.rightArm;
            var10000.pitch += -0.62831855F;
            var10000 = this.leftArm;
            var10000.pitch += -0.62831855F;
            this.rightLeg.pitch = -1.4137167F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.4137167F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;
        }

        this.rightArm.yaw = 0.0F;
        this.leftArm.yaw = 0.0F;
        boolean bl3 = livingEntity.getMainArm() == Arm.RIGHT;
        boolean bl4;
        if (livingEntity.isUsingItem()) {
            bl4 = livingEntity.getActiveHand() == Hand.MAIN_HAND;
            if (ShieldFixMod.getShieldingManager().isHoldingShield(livingEntity))
            {
                this.positionRightArm(livingEntity);
                this.positionLeftArm(livingEntity);
            } else {
                if (bl4 == bl3) {
                    this.positionRightArm(livingEntity);
                } else {
                    this.positionLeftArm(livingEntity);
                }
            }
        } else {
            bl4 = bl3 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if (bl3 != bl4) {
                this.positionLeftArm(livingEntity);
                this.positionRightArm(livingEntity);
            } else {
                this.positionRightArm(livingEntity);
                this.positionLeftArm(livingEntity);
            }
        }

        this.animateArms(livingEntity, h);
        if (this.sneaking) {
            this.body.pitch = 0.5F;
            var10000 = this.rightArm;
            var10000.pitch += 0.4F;
            var10000 = this.leftArm;
            var10000.pitch += 0.4F;
            this.rightLeg.pivotZ = 4.0F;
            this.leftLeg.pivotZ = 4.0F;
            this.rightLeg.pivotY = 12.2F;
            this.leftLeg.pivotY = 12.2F;
            this.head.pivotY = 4.2F;
            this.body.pivotY = 3.2F;
            this.leftArm.pivotY = 5.2F;
            this.rightArm.pivotY = 5.2F;
        } else {
            this.body.pitch = 0.0F;
            this.rightLeg.pivotZ = 0.0F;
            this.leftLeg.pivotZ = 0.0F;
            this.rightLeg.pivotY = 12.0F;
            this.leftLeg.pivotY = 12.0F;
            this.head.pivotY = 0.0F;
            this.body.pivotY = 0.0F;
            this.leftArm.pivotY = 2.0F;
            this.rightArm.pivotY = 2.0F;
        }

        if (this.rightArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
            CrossbowPosing.swingArm(this.rightArm, h, 1.0F);
        }

        if (this.leftArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
            CrossbowPosing.swingArm(this.leftArm, h, -1.0F);
        }

        if (this.leaningPitch > 0.0F) {
            float l = f % 26.0F;
            Arm arm = this.getPreferredArm(livingEntity);
            float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
            float n = arm == Arm.LEFT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
            float o;
            if (!livingEntity.isUsingItem()) {
                if (l < 14.0F) {
                    this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 0.0F);
                    this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
                    this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                    this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                    this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                    this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                } else if (l >= 14.0F && l < 22.0F) {
                    o = (l - 14.0F) / 8.0F;
                    this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F * o);
                    this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F * o);
                    this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                    this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                    this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 5.012389F - 1.8707964F * o);
                    this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * o);
                } else if (l >= 22.0F && l < 26.0F) {
                    o = (l - 22.0F) / 4.0F;
                    this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F - 1.5707964F * o);
                    this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F - 1.5707964F * o);
                    this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                    this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                    this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F);
                    this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F);
                }
            }

            o = 0.3F;
            float p = 0.33333334F;
            this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.leftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + 3.1415927F));
            this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.rightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
        }

        this.hat.copyTransform(this.head);
    }
}
