package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.ShieldStateManager;

@Mixin(IsUsingItem.class)
public class IsUsingItemMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void registerShieldItem(ItemStack itemStack, ClientLevel level, LivingEntity owner, int seed, ItemDisplayContext displayContext, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.modEnabled) return;
        if (owner == Minecraft.getInstance().player) return;
        if (owner instanceof Player player) {
            cir.setReturnValue(ShieldStateManager.isUsingShield(player, Config.factorDelay));
        }
    }
}
