package walksy.shield.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shield.main.ShieldFixMod;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "handleStatus", at = @At("HEAD"))
    public void handleByteStatus(byte status, CallbackInfo ci)
    {
        ShieldFixMod.getShieldingManager().handleByteStatus(status, this);
    }
}
