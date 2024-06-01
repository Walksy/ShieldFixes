package walksy.shield.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.manager.PlayerShieldingManager;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {


    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        PlayerShieldingManager.INSTANCE.tick();
    }
}
