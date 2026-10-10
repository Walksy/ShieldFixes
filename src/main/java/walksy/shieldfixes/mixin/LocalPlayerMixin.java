package walksy.shieldfixes.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shieldfixes.config.ShieldFixesConfig;
import walksy.shieldfixes.manager.ShieldStateManager;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (!ShieldFixesConfig.modEnabled) {
            return;
        }
        ShieldStateManager.syncRemoteShieldUse();
    }
}
