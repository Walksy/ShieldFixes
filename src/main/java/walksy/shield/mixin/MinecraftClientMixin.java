package walksy.shield.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shield.main.ShieldFixMod;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "doAttack", at = @At("HEAD"))
    public void attackEntity(CallbackInfoReturnable<Boolean> cir)
    {
        ShieldFixMod.getShieldingManager().onAttackEntity();
    }
}
