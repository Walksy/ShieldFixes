package walksy.shield.mixin;

import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.manager.PlayerShieldingManager;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow
    protected abstract void positionRightArm(T entity);

    @Shadow
    protected abstract void positionLeftArm(T entity);

    @Inject(method = "positionRightArm", at = @At(value = "HEAD"))
    private void positionRightArm(T entity, CallbackInfo ci)
    {
        boolean bl3 = entity.getMainArm() == Arm.RIGHT;
        boolean bl4 = entity.getActiveHand() == Hand.MAIN_HAND;
        if (bl4 == bl3) {
            if (PlayerShieldingManager.INSTANCE.isHoldingUsableShield(entity)) {
                this.positionLeftArm(entity);
            }
        }
    }
    @Inject(method = "positionLeftArm", at = @At(value = "HEAD"))
    private void positionLeftArm(T entity, CallbackInfo ci)
    {
        boolean bl3 = entity.getMainArm() == Arm.RIGHT;
        boolean bl4 = entity.getActiveHand() == Hand.MAIN_HAND;
        if (bl4 != bl3) {
            if (PlayerShieldingManager.INSTANCE.isHoldingUsableShield(entity)) {
                this.positionRightArm(entity);
            }
        }
    }
}
