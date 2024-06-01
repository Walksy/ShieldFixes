package walksy.shield.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.main.ShieldFixMod;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
        ordinal = 0), method = "tick()V")
    private void playerTick(CallbackInfo ci)
    {
        ShieldFixMod.getShieldingManager().tick();
    }
}
