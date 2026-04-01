package walksy.shieldfixes.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import walksy.shieldfixes.ShieldFixes;
import walksy.shieldfixes.config.Config;

import java.util.List;

public class EnvironmentEffectShieldManager {

    private final Minecraft minecraft = Minecraft.getInstance();

    public void onExplosion(WorldExplosion explosion) {
        if (!explosion.level().isClientSide() || this.minecraft.player == null) return;

        for (Player affectedPlayer : getNearPlayers(explosion)) {
            if (affectedPlayer.isAlive()) {
                double distance = Math.sqrt(affectedPlayer.distanceToSqr(explosion.pos));
                if (distance < explosion.getMaxDistance()) {
                    if (ShieldStateManager.isUsingShield(affectedPlayer, explosion.pos(), Config.factorDelay)) {
                        this.sound(affectedPlayer, SoundEvents.SHIELD_BLOCK.value());
                    }
                }
            }
        }
    }

    public void onDisable(LivingEntity livingEntity) {
        this.sound(livingEntity, SoundEvents.SHIELD_BREAK.value());
    }

    public void onAttack(Player target) {
        if (ShieldStateManager.isUsingShield(target, this.minecraft.player.position(), true) && !ShieldStateManager.disablesShield(this.minecraft.player)) {
            this.sound(target, SoundEvents.SHIELD_BLOCK.value());
        }
    }

    private static List<Player> getNearPlayers(WorldExplosion explosion) {
       return explosion.level.getEntitiesOfClass(Player.class, explosion.getBoundingBox(), obj -> true);
    }

    private void sound(LivingEntity entity, SoundEvent soundEvent) {
        if (this.minecraft.level == null) return;
        this.minecraft.level.playSound(
            entity,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            soundEvent,
            entity.getSoundSource(),
            1F,
            0.8F + Minecraft.getInstance().level.getRandom().nextFloat() * 0.4F
        );
    }

    public record WorldExplosion(Vec3 pos, Level level, float power) {

        public float getMaxDistance() {
            return this.power * 2;
        }

        public AABB getBoundingBox() {
            float maxDistance = this.getMaxDistance();
            double x = this.pos.x;
            double y = this.pos.y;
            double z = this.pos.z;
            int x1 = Mth.floor(x - maxDistance - 1.0);
            int x2 = Mth.floor(x + maxDistance + 1.0);
            int y1 = Mth.floor(y - maxDistance - 1.0);
            int y2 = Mth.floor(y + maxDistance + 1.0);
            int z1 = Mth.floor(z - maxDistance - 1.0);
            int z2 = Mth.floor(z + maxDistance + 1.0);

            return new AABB(x1, y1, z1, x2, y2, z2);
        }
    }
}
