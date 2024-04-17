package walksy.shield.manager;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.*;
import org.joml.Math;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shield.main.ShieldFixMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerShieldingManager {

    public ArrayList<ShieldingPlayer> shieldingPlayers = new ArrayList<>();
    public ArrayList<DisabledShieldPlayer> disabledShieldPlayers = new ArrayList<>();

    /**
     * This code uses the #usingShield(LivingEntity) method to collect an arraylist of players shielding
     * However they're not actually shielding due to it not factoring in the 5 tick delay
     * So we factor this in ourselves (See #ShieldingPlayer)
     */
    public void tick() {
        shieldingPlayers.removeIf(shieldingPlayer -> !usingShield(shieldingPlayer.getPlayer()));
        shieldingPlayers.forEach(ShieldingPlayer::tick);
        disabledShieldPlayers.removeIf(disabledShieldPlayer -> disabledShieldPlayer.disabledTime <= 0);
        disabledShieldPlayers.forEach(DisabledShieldPlayer::tick);

        MinecraftClient.getInstance().world.getPlayers().forEach(player -> {
            if (usingShield(player) && player != MinecraftClient.getInstance().player) {
                boolean playerAlreadyExists = false;
                for (ShieldingPlayer shieldingPlayer : shieldingPlayers) {
                    if (shieldingPlayer.getPlayer() == player) {
                        playerAlreadyExists = true;
                        break;
                    }
                }
                if (!playerAlreadyExists) {
                    shieldingPlayers.add(new ShieldingPlayer(player));
                }
            }
        });
    }



    public void handleByteStatus(byte status, Object castedClass)
    {
        LivingEntity entity = LivingEntity.class.cast(castedClass);
        switch (status)
        {
            case 29 -> { //Shield Block
                MinecraftClient.getInstance().world.playSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.ITEM_SHIELD_BLOCK,
                    SoundCategory.PLAYERS,
                    1F,
                    0.8F + MinecraftClient.getInstance().world.random.nextFloat() * 0.4F,
                    false
                );
            }
            case 30 -> { //Disabled Shield
                disabledShieldPlayers.add(new DisabledShieldPlayer(entity));
                MinecraftClient.getInstance().world.playSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.ITEM_SHIELD_BREAK,
                    SoundCategory.PLAYERS,
                    1F,
                    0.8F + MinecraftClient.getInstance().world.random.nextFloat() * 0.4F,
                    false
                );
            }
        }
    }

    public void onExplosion(double x, double y, double z, float power, World world)
    {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (!world.isClient || player == null) return;

        double maxDistance = power * 2;
        Vec3d pos = new Vec3d(x, y, z);

        int x1 = MathHelper.floor(x - maxDistance - 1.0);
        int x2 = MathHelper.floor(x + maxDistance + 1.0);
        int y1 = MathHelper.floor(y - maxDistance - 1.0);
        int y2 = MathHelper.floor(y + maxDistance + 1.0);
        int z1 = MathHelper.floor(z - maxDistance - 1.0);
        int z2 = MathHelper.floor(z + maxDistance + 1.0);
        List<LivingEntity> nearEntities = world.getEntitiesByClass(LivingEntity.class, new Box(x1, y1, z1, x2, y2, z2), Objects::nonNull);

        for (LivingEntity nearEntity : nearEntities) {
            if (nearEntity.isAlive()) {
                double distance = Math.sqrt(nearEntity.squaredDistanceTo(pos));
                if (distance < maxDistance) {
                    shieldingPlayers.forEach(shieldingPlayer -> {
                        if (shieldingPlayer.getPlayer() != nearEntity) return;
                        Vec3d rotation = shieldingPlayer.getPlayer().getRotationVec(1);
                        Vec3d relativePosition = pos.relativize(shieldingPlayer.getPlayer().getPos()).normalize();
                        Vec3d flat = new Vec3d(relativePosition.x, 0.0, relativePosition.z);
                        if (flat.dotProduct(rotation) < 0.0) {
                            if (shieldingPlayer.actuallyShielding()) {
                                world.playSound(
                                    nearEntity.getX(),
                                    nearEntity.getY(),
                                    nearEntity.getZ(),
                                    SoundEvents.ITEM_SHIELD_BLOCK,
                                    nearEntity.getSoundCategory(),
                                    1F,
                                    0.8F + MinecraftClient.getInstance().world.random.nextFloat() * 0.4F,
                                    false
                                );
                            }
                        }
                    });
                }
            }
        }
    }

    public void setArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        cir.cancel();
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        } else {
            if (itemStack.isOf(Items.SHIELD)) {
                for (ShieldingPlayer shieldingPlayer : ShieldFixMod.getShieldingManager().shieldingPlayers) {
                    if (shieldingPlayer.actuallyShielding()) {
                        if (shieldingPlayer.getPlayer() == player) {
                            cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
                            return;
                        }
                    } else {
                        cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
                        return;
                    }
                }
            }
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();

                if (player == MinecraftClient.getInstance().player)
                {
                    if (useAction == UseAction.BLOCK)
                    {
                        cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
                        return;
                    }
                }

                if (useAction == UseAction.BOW) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
                    return;
                }
                if (useAction == UseAction.SPEAR) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.THROW_SPEAR);
                    return;
                }
                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
                    return;
                }
                if (useAction == UseAction.SPYGLASS) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.SPYGLASS);
                    return;
                }
                if (useAction == UseAction.TOOT_HORN) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.TOOT_HORN);
                    return;
                }
                if (useAction == UseAction.BRUSH) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.BRUSH);
                    return;
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
                return;
            }
            cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
        }
    }
/*
    public void renderShield(ShieldEntityModel shieldModel, ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        if (stack.isOf(Items.SHIELD)) {
            if (mode != ModelTransformationMode.GUI
                && mode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND
                && mode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
                && mode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND
                && mode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND
            ) return;
            LivingEntity shieldingEntity = null;
            for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                if (!(entity instanceof LivingEntity livingEntity))
                    continue;
                if (livingEntity.getOffHandStack().equals(stack) || livingEntity.getMainHandStack().equals(stack)) {
                    shieldingEntity = livingEntity;
                    break;
                }
            }
            if (shieldingEntity == null)
                return;
            ci.cancel();
            float red = 255, green = 255, blue = 255;

            for (DisabledShieldPlayer disabledShieldPlayer : disabledShieldPlayers)
            {
                if (shieldingEntity != disabledShieldPlayer.player) return;
                red = 255;
                green = 0;
                blue = 0;
            }

            for (ShieldingPlayer shieldingPlayer : shieldingPlayers)
            {
                if (shieldingEntity != shieldingPlayer.getPlayer()) return;
                if (shieldingPlayer.actuallyShielding()) {
                    red = 0;
                    green = 255;
                    blue = 0;
                } else {
                    red = 255;
                    green = 255;
                    blue = 255;
                }
            }

            boolean bl = BlockItem.getBlockEntityNbt(stack) != null;
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            SpriteIdentifier spriteIdentifier = bl ? ModelLoader.SHIELD_BASE : ModelLoader.SHIELD_BASE_NO_PATTERN;
            VertexConsumer vertexConsumer = spriteIdentifier.getSprite().getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, shieldModel.getLayer(spriteIdentifier.getAtlasId()), true, stack.hasGlint())
            );
            shieldModel.getHandle().render(matrices, vertexConsumer, light, overlay, red / 255, green / 255, blue / 255, 1.0F);
            if (bl) {
                List<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(ShieldItem.getColor(stack), BannerBlockEntity.getPatternListNbt(stack));
                BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, shieldModel.getPlate(), spriteIdentifier, false, list, stack.hasGlint());
            } else {
                shieldModel.getPlate().render(matrices, vertexConsumer, light, overlay, red / 255, green / 255, blue / 255, 1.0F);
            }
            matrices.pop();
        }


    }

*/
    boolean isHoldingShield(LivingEntity entity)
    {
        return entity.getMainHandStack().isOf(Items.SHIELD) || entity.getOffHandStack().isOf(Items.SHIELD);
    }

    /**
     * This boolean doesn't actually return the accurate state of a player shielding or not
     * However the LivingEntity.isBlocking() method is also inaccurate due to the client not recording the player's itemUseTime correctly
     */
    boolean usingShield(LivingEntity entity)
    {
        return entity.isUsingItem() && isHoldingShield(entity);
    }

}
