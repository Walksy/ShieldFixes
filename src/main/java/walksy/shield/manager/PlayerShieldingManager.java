package walksy.shield.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import walksy.shield.main.ILivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerShieldingManager {

    public static PlayerShieldingManager INSTANCE = new PlayerShieldingManager();
    private final Map<Entity, Integer> entityUseTimeMap = new HashMap<>();

    public void tick() {
        MinecraftClient.getInstance().world.getEntities().forEach(entity -> {

            if (entity instanceof PlayerEntity player && player != MinecraftClient.getInstance().player) {
                int useTime = entityUseTimeMap.getOrDefault(entity, 72000); //create an integer map with each instance of a player

                if (player.isUsingItem() && isHoldingUsableShield(player)) {
                    ((ILivingEntity) player).setActiveItem(player.getStackInHand(player.getActiveHand()));
                    ((ILivingEntity) player).setItemUseTime(useTime);
                    useTime--;
                } else {
                    useTime = 72000;
                }

                entityUseTimeMap.put(entity, useTime);
            }
        });
    }

    public void onAttack()
    {
        PlayerEntity me = MinecraftClient.getInstance().player;

        if (me.disablesShield()) return; //if holding an axe, don't continue - We use the entityByteStatus to play shield disable sounds
        ItemStack itemStack = me.getStackInHand(Hand.MAIN_HAND); //can only attack with mainHand (duh?)
        if (!itemStack.isItemEnabled(MinecraftClient.getInstance().world.getEnabledFeatures())) return;

        if (MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof PlayerEntity target)
        {
            if (target.isBlocking()) {
                Vec3d rotation = target.getRotationVec(1);
                Vec3d relativePosition = me.getPos().relativize(target.getPos()).normalize();
                Vec3d flat = new Vec3d(relativePosition.x, 0.0, relativePosition.z);
                if (flat.dotProduct(rotation) < 0.0) {
                    MinecraftClient.getInstance().world.playSound(
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        SoundEvents.ITEM_SHIELD_BLOCK,
                        SoundCategory.PLAYERS,
                        1F,
                        0.8F + MinecraftClient.getInstance().world.random.nextFloat() * 0.4F,
                        false
                    );
                }
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
                    Vec3d rotation = nearEntity.getRotationVec(1);
                    Vec3d relativePosition = pos.relativize(nearEntity.getPos()).normalize();
                    Vec3d flat = new Vec3d(relativePosition.x, 0.0, relativePosition.z);
                    if (flat.dotProduct(rotation) < 0.0) {
                        if (nearEntity.isBlocking()) {
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
                }
            }
        }
    }

    public void handleByteStatus(byte status, Object castedClass)
    {
        LivingEntity entity = LivingEntity.class.cast(castedClass);
        if (status == 30) {//Disabled Shield
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

    public void setArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir)
    {
        if (player == MinecraftClient.getInstance().player) return;
        cir.cancel();
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (ConfigManager.INSTANCE.factor5TickDelay) {
                    if (useAction == UseAction.BLOCK && player.isBlocking()) {
                        cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
                        return;
                    }
                } else {
                    if (useAction == UseAction.BLOCK) {
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
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack) && !player.isBlocking()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
                return;
            }

            cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
        }
    }

    public boolean isHoldingUsableShield(LivingEntity entity)
    {
        return (entity.getMainHandStack().isOf(Items.SHIELD) || entity.getOffHandStack().isOf(Items.SHIELD)) && !isHoldingAnimationItemMainHand(entity);
    }

    private boolean isHoldingAnimationItemMainHand(LivingEntity entity)
    {
        return entity.getMainHandStack().getMaxUseTime() != 0 //any time greater than 0 has some sort of animation
            && !entity.getMainHandStack().isOf(Items.SHIELD); //leave out the shield
    }
}
