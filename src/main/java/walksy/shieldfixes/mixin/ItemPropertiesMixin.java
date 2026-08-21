package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.ShieldStateManager;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemProperties.class)
public class ItemPropertiesMixin {

    @Shadow
    @Final
    private static Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES;

    @Inject(method = "register(Lnet/minecraft/world/item/Item;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/item/ClampedItemPropertyFunction;)V", at = @At("HEAD"), cancellable = true)
    private static void registerShieldItem(Item item, ResourceLocation id, ClampedItemPropertyFunction provider, CallbackInfo ci) {
        if (item != Items.SHIELD) return;
        ci.cancel();
        shieldfixes$register(Items.SHIELD, ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, seed) -> {
            if (entity == null) return 0.0F;
            if (Config.modEnabled && entity != Minecraft.getInstance().player && entity instanceof Player target) {
                return ShieldStateManager.isUsingShield(target, Config.factorDelay) ? 1.0F : 0.0F;
            }
            return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });
    }

    @Unique
    private static void shieldfixes$register(Item item, ResourceLocation id, ClampedItemPropertyFunction provider) {
        PROPERTIES.computeIfAbsent(item, key -> new HashMap<>()).put(id, provider);
    }
}
