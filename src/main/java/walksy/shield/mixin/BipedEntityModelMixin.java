package walksy.shield.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.ShieldBlockMod;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    @Final
    public ModelPart head;

    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void positionRightArm(T entity, CallbackInfo ci)
    {
        ci.cancel();
        switch (this.rightArmPose) {
            case EMPTY:
                this.rightArm.yaw = 0.0F;
                break;
            //case BLOCK:
            //    this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F; //The 'BLOCK' check in this scenario does not work
            //    this.rightArm.yaw = -0.5235988F;
            //    break;
            case ITEM:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.31415927F;
                this.rightArm.yaw = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 3.1415927F;
                this.rightArm.yaw = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yaw = -0.1F + this.head.yaw;
                this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
                this.rightArm.pitch = -1.5707964F + this.head.pitch;
                this.leftArm.pitch = -1.5707964F + this.head.pitch;
                break;
            case CROSSBOW_CHARGE:
                CrossbowPosing.charge(this.rightArm, this.leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
                break;
            case BRUSH:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.62831855F;
                this.rightArm.yaw = 0.0F;
                break;
            case SPYGLASS:
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (entity.isInSneakingPose() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.rightArm.yaw = this.head.yaw - 0.2617994F;
                break;
            case TOOT_HORN:
                this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
                this.rightArm.yaw = this.head.yaw - 0.5235988F;
        }

        if (ShieldBlockMod.getShieldingManager().playersShielding$Server.contains(entity)) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
            this.rightArm.yaw = -0.5235988F;
        }
    }
}
