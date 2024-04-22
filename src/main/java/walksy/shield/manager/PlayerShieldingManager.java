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
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
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

import java.util.*;

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


    public void onAttackEntity()
    {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player.disablesShield()) return;
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isItemEnabled(MinecraftClient.getInstance().world.getEnabledFeatures())) return;
        if (MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult hitResult) {
            shieldingPlayers.forEach(shieldingPlayer ->
            {
                if (shieldingPlayer.actuallyShielding() && hitResult.getEntity() == shieldingPlayer.getPlayer()) {
                    Vec3d rotation = shieldingPlayer.getPlayer().getRotationVec(1);
                    Vec3d relativePosition = player.getPos().relativize(shieldingPlayer.getPlayer().getPos()).normalize();
                    Vec3d flat = new Vec3d(relativePosition.x, 0.0, relativePosition.z);
                    if (flat.dotProduct(rotation) < 0.0) {
                        MinecraftClient.getInstance().world.playSound(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ITEM_SHIELD_BLOCK,
                            SoundCategory.PLAYERS,
                            1F,
                            0.8F + MinecraftClient.getInstance().world.random.nextFloat() * 0.4F,
                            false
                        );
                    }
                }
            });
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


    public boolean isHoldingShield(LivingEntity entity)
    {
        return entity.getMainHandStack().isOf(Items.SHIELD) || entity.getOffHandStack().isOf(Items.SHIELD);
    }

    public boolean isHoldingAnimationItemMainHand(LivingEntity entity)
    {
        return entity.getMainHandStack().isOf(Items.CROSSBOW)
            || entity.getMainHandStack().isOf(Items.GOLDEN_APPLE)
            || entity.getMainHandStack().isOf(Items.BOW);
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
