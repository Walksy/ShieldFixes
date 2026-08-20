package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.ShieldStateManager;

@Mixin(PlayerRenderer.class)
public class PlayerRenderStateMixin {

    @Inject(method = "getArmPose(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState$HandState;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(PlayerRenderState state, PlayerRenderState.HandState handState, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!Config.modEnabled) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (!(minecraft.level.getEntity(state.id) instanceof Player player) || player == minecraft.player) return;

        cir.cancel();
        boolean usingShield = ShieldStateManager.isUsingShield(player, Config.factorDelay);
        ItemStack stack = player.getItemInHand(hand);
        if (handState.isEmpty) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        } else {
            if (state.useItemHand == hand && state.useItemRemainingTicks > 0) {
                ItemUseAnimation useAction = handState.useAnimation;
                if (useAction == ItemUseAnimation.BLOCK && (usingShield || !stack.is(Items.SHIELD))) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
                    return;
                }

                if (useAction == ItemUseAnimation.BOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
                    return;
                }

                if (useAction == ItemUseAnimation.SPEAR) {
                    cir.setReturnValue(HumanoidModel.ArmPose.THROW_SPEAR);
                    return;
                }

                if (useAction == ItemUseAnimation.CROSSBOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                    return;
                }

                if (useAction == ItemUseAnimation.SPYGLASS) {
                    cir.setReturnValue(HumanoidModel.ArmPose.SPYGLASS);
                    return;
                }

                if (useAction == ItemUseAnimation.TOOT_HORN) {
                    cir.setReturnValue(HumanoidModel.ArmPose.TOOT_HORN);
                    return;
                }

                if (useAction == ItemUseAnimation.BRUSH) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BRUSH);
                    return;
                }
            } else if (!state.swinging && handState.holdsChargedCrossbow && !usingShield) {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
                return;
            }

            cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
        }
    }
}
