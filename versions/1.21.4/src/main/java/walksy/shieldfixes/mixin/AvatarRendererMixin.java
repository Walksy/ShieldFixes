package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.config.ShieldFixesConfig;
import walksy.shieldfixes.manager.ShieldStateManager;

@Mixin(PlayerRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(final Player player, final ItemStack stack, final InteractionHand hand, final CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!ShieldFixesConfig.modEnabled || player == Minecraft.getInstance().player) {
            return;
        }
        final boolean usingShield = ShieldStateManager.isUsingShield(player, ShieldFixesConfig.factorDelay);
        if (stack.isEmpty()) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
            return;
        }
        if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
            final ItemUseAnimation useAnimation = stack.getUseAnimation();
            if (useAnimation == ItemUseAnimation.BLOCK && (usingShield || !stack.is(Items.SHIELD))) {
                cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
                return;
            }
            if (useAnimation == ItemUseAnimation.BOW) {
                cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
                return;
            }
            if (useAnimation == ItemUseAnimation.SPEAR) {
                cir.setReturnValue(HumanoidModel.ArmPose.THROW_SPEAR);
                return;
            }
            if (useAnimation == ItemUseAnimation.CROSSBOW) {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                return;
            }
            if (useAnimation == ItemUseAnimation.SPYGLASS) {
                cir.setReturnValue(HumanoidModel.ArmPose.SPYGLASS);
                return;
            }
            if (useAnimation == ItemUseAnimation.TOOT_HORN) {
                cir.setReturnValue(HumanoidModel.ArmPose.TOOT_HORN);
                return;
            }
            if (useAnimation == ItemUseAnimation.BRUSH) {
                cir.setReturnValue(HumanoidModel.ArmPose.BRUSH);
                return;
            }
        } else if (!player.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack) && !usingShield) {
            cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
            return;
        }
        cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
    }
}