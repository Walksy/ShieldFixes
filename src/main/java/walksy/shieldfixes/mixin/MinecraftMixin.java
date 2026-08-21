package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.ShieldFixes;
import walksy.shieldfixes.config.Config;

@Mixin(Minecraft.class)
public class MinecraftMixin {


    @Shadow
    public Entity crosshairPickEntity;

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V"))
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.modEnabled) return;
        if (this.crosshairPickEntity instanceof Player target) {
            ShieldFixes.getEnvironmentEffectShieldManager().onAttack(target);
        }
    }
}
