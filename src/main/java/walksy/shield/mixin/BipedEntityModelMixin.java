package walksy.shield.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    public BipedEntityModel.ArmPose leftArmPose;

    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void positionRightArm(T entity, CallbackInfo ci) {
        ci.cancel();
        switch(this.rightArmPose) {
            case EMPTY:
                this.rightArm.yaw = 0.0F;
                break;
            case BLOCK:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
                this.rightArm.yaw = (float) (-Math.PI / 6);
                break;
            case ITEM:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) (Math.PI / 10);
                this.rightArm.yaw = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
                this.rightArm.yaw = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yaw = -0.1F + this.head.yaw;
                this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
                this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
                this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
                break;
            case CROSSBOW_CHARGE:
                CrossbowPosing.charge(this.rightArm, this.leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
                break;
            case BRUSH:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) (Math.PI / 5);
                this.rightArm.yaw = 0.0F;
                break;
            case SPYGLASS:
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (entity.isInSneakingPose() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.rightArm.yaw = this.head.yaw - (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
                this.rightArm.yaw = this.head.yaw - (float) (Math.PI / 6);
        }
    }

    //TODO Fix Left Hand Glitch

    @Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
    private void positionLeftArm(T entity, CallbackInfo ci) {
        ci.cancel();
        this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 10);
        this.leftArm.yaw = 0.0F;
        /*
        switch(this.leftArmPose) {
            case EMPTY:
                this.leftArm.yaw = 0.0F;
                break;
            case BLOCK:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.9424779F;
                this.leftArm.yaw = (float) (Math.PI / 6);
                break;
            case ITEM:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 10);
                this.leftArm.yaw = 0.0F;
                break;
            case THROW_SPEAR:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
                this.leftArm.yaw = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
                this.leftArm.yaw = 0.1F + this.head.yaw;
                this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
                this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
                break;
            case CROSSBOW_CHARGE:
                CrossbowPosing.charge(this.rightArm, this.leftArm, entity, false);
                break;
            case CROSSBOW_HOLD:
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
                break;
            case BRUSH:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 5);
                this.leftArm.yaw = 0.0F;
                break;
            case SPYGLASS:
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (entity.isInSneakingPose() ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.leftArm.yaw = this.head.yaw + (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
                this.leftArm.yaw = this.head.yaw + (float) (Math.PI / 6);
        }

         */
    }
}
