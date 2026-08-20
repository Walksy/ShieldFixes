package walksy.shieldfixes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=1.21.11 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;
//?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;

import static net.minecraft.world.item.ItemUseAnimation.*;
//?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import static net.minecraft.world.item.ItemUseAnimation.*;
*///?} else {
/*import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import static net.minecraft.world.item.UseAnim.*;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.ShieldStateManager;

//? if >=1.21.9 {
@Mixin(AvatarRenderer.class)
//?} else
//@Mixin(PlayerRenderer.class)
public class AvatarRendererMixin {

    //? if >=1.21.9 {
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(Avatar avatar, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!Config.modEnabled) return;
        LocalPlayer local = Minecraft.getInstance().player;
        if (avatar == local || !(avatar instanceof Player player)) return;
    //?} elif >=1.21.4 {
    /*@Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(Player player, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!Config.modEnabled) return;
        LocalPlayer local = Minecraft.getInstance().player;
        if (player == local) return;
    *///?} else {
    /*@Inject(method = "getArmPose(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void setArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!Config.modEnabled) return;
        LocalPlayer local = Minecraft.getInstance().player;
        if (player == local) return;
        ItemStack stack = player.getItemInHand(hand);
    *///?}
        cir.cancel();
        boolean usingShield = ShieldStateManager.isUsingShield(player, Config.factorDelay);
        if (stack.isEmpty()) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        //? if >=1.21.5 {
        } else if (!player.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack) && !usingShield) {
            cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
        //?}
        } else {
            if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
                var useAction = stack.getUseAnimation();
                if (useAction == BLOCK && (usingShield || !stack.is(Items.SHIELD))) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
                    return;
                }

                if (useAction == BOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
                    return;
                }

                if (useAction == SPEAR) {
                    //? if >=1.21.11 {
                    cir.setReturnValue(HumanoidModel.ArmPose.SPEAR);
                    //?} else
                    //cir.setReturnValue(HumanoidModel.ArmPose.THROW_SPEAR);
                    return;
                }

                //? if >=1.21.11 {
                if (useAction == TRIDENT) {
                    cir.setReturnValue(HumanoidModel.ArmPose.THROW_TRIDENT);
                    return;
                }
                //?}

                if (useAction == CROSSBOW) {
                    cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_CHARGE);
                    return;
                }

                if (useAction == SPYGLASS) {
                    cir.setReturnValue(HumanoidModel.ArmPose.SPYGLASS);
                    return;
                }

                if (useAction == TOOT_HORN) {
                    cir.setReturnValue(HumanoidModel.ArmPose.TOOT_HORN);
                    return;
                }

                if (useAction == BRUSH) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BRUSH);
                    return;
                }
            //? if <1.21.5 {
            /*// Vanilla only falls back to the charged crossbow pose when no item is being used.
            } else if (!player.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack) && !usingShield) {
                cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
                return;
            *///?}
            }

            //? if >=1.21.11 {
            SwingAnimation swingAnimation = stack.get(DataComponents.SWING_ANIMATION);
            if (swingAnimation != null && swingAnimation.type() == SwingAnimationType.STAB && player.swinging) {
                cir.setReturnValue(HumanoidModel.ArmPose.SPEAR);
                return;
            }

            if (stack.is(ItemTags.SPEARS)) {
                cir.setReturnValue(HumanoidModel.ArmPose.SPEAR);
                return;
            }
            //?}

            cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
        }
    }
}
