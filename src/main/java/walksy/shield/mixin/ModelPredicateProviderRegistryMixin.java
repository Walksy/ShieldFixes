package walksy.shield.mixin;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.main.ShieldFixMod;
import walksy.shield.manager.ShieldingPlayer;

import java.util.Map;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderRegistryMixin {

    @Shadow
    @Final
    private static Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC;

    @Inject(method = "register(Lnet/minecraft/item/Item;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/item/ClampedModelPredicateProvider;)V", at = @At("HEAD"), cancellable = true)
    private static void registerShieldItem(Item item, Identifier id, ClampedModelPredicateProvider provider, CallbackInfo ci) {
        if (item == Items.SHIELD) {
            ci.cancel();
            overrideRegisterMethod(Items.SHIELD, new Identifier("blocking"), (stack, world, entity, seed) -> {
                    if (entity != null) {
                        if (entity == MinecraftClient.getInstance().player && entity.isUsingItem()) {
                            if (entity.getActiveItem() == stack) {
                                return 1.0F;
                            }
                        } else {
                            for (ShieldingPlayer shieldingPlayer : ShieldFixMod.getShieldingManager().shieldingPlayers) {
                                if (shieldingPlayer.getPlayer() == entity && shieldingPlayer.actuallyShielding()) {
                                    return 1.0F;
                                }
                            }
                        }
                    } else {
                        return 0.0F;
                    }
                    return 0.0F;
                });

                /*
                if (entity == MinecraftClient.getInstance().player)
                {
                    if (entity.isUsingItem())
                    {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
                for (ShieldingPlayer shieldingPlayer : ShieldFixMod.getShieldingManager().shieldingPlayers) {
                    if (shieldingPlayer.actuallyShielding()) {
                        return 1.0F;
                    }
                }
                return 0.0F;

                 */
        }
    }


    @Unique
    private static void overrideRegisterMethod(Item item, Identifier id, ClampedModelPredicateProvider provider)
    {
        ((Map)ITEM_SPECIFIC.computeIfAbsent(item, (key) -> {
            return Maps.newHashMap();
        })).put(id, provider);
    }
}
