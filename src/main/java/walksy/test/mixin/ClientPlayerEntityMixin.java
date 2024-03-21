package walksy.test.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.test.TestMod;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
        ordinal = 0), method = "tick()V")
    private void playerTick(CallbackInfo ci)
    {
        if (!TestMod.enabled) return;
        MinecraftClient.getInstance().world.getEntities().forEach(entity -> {
            if (entity instanceof LivingEntity player && player != MinecraftClient.getInstance().player) {

                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(String.valueOf("Custom func(): " + walksyBlocking(player) + " ---" + "Minecraft Func(): " + player.isBlocking())));
                //MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(player.getName() + " Item Use Time: " + player.getItemUseTime() + "- Is Using Item: " + player.isUsingItem()));
                //if (!player.isBlocking()) return;
                //MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(player.getName() + " is using a shield " + MinecraftClient.getInstance().world.getRandom().nextBetween(0, 10)));
            }
        });
    }

    @Unique
    protected boolean walksyBlocking(LivingEntity entity)
    {
        return entity.isUsingItem() && entity.getMainHandStack().isOf(Items.SHIELD);
    }
}
