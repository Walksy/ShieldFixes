package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.ShieldStateManager;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(Avatar avatar, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!Config.modEnabled) return;
        LocalPlayer local = Minecraft.getInstance().player;
        if (avatar == local || !(avatar instanceof Player player)) return;
        cir.cancel();
        boolean usingShield = ShieldStateManager.isUsingShield(player, Config.factorDelay);
        if (stack.isEmpty()) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        } else if (!player.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack) && !usingShield) {
            cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
        } else {
            if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
                ItemUseAnimation useAction = stack.getUseAnimation();
                if (useAction == ItemUseAnimation.BLOCK && usingShield) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
                    return;
                }

                if (useAction == ItemUseAnimation.BOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
                    return;
                }

                if (useAction == ItemUseAnimation.SPEAR) {
                    cir.setReturnValue(HumanoidModel.ArmPose.SPEAR);
                    return;
                }

                if (useAction == ItemUseAnimation.CROSSBOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                    return;
                }

                if (useAction == ItemUseAnimation.SPYGLASS) {
                    cir.setReturnValue(HumanoidModel.ArmPose.SPYGLASS);
                }

                if (useAction == ItemUseAnimation.TOOT_HORN) {
                    cir.setReturnValue(HumanoidModel.ArmPose.TOOT_HORN);
                    return;
                }

                if (useAction == ItemUseAnimation.BRUSH) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BRUSH);
                    return;
                }
            }

            cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
        }
    }
}
