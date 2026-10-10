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
import walksy.shieldfixes.config.ShieldFixesConfig;

import java.util.List;

public class EnvironmentEffectShieldManager {
    private final Minecraft minecraft = Minecraft.getInstance();

    public void onExplosion(final WorldExplosion explosion) {
        if (!explosion.level().isClientSide() || this.minecraft.player == null) {
            return;
        }
        for (final Player affectedPlayer : getNearPlayers(explosion)) {
            if (!affectedPlayer.isAlive()) {
                continue;
            }
            final double distance = Math.sqrt(affectedPlayer.distanceToSqr(explosion.pos()));
            if (distance < explosion.getMaxDistance() && ShieldStateManager.isUsingShield(affectedPlayer, explosion.pos(), ShieldFixesConfig.factorDelay)) {
                this.sound(affectedPlayer, shieldBlock());
            }
        }
    }

    public void onDisable(final LivingEntity livingEntity) {
        this.sound(livingEntity, shieldBreak());
    }

    public void onAttack(final Player target) {
        if (ShieldStateManager.isUsingShield(target, this.minecraft.player.position(), true) && !ShieldStateManager.disablesShield(this.minecraft.player)) {
            this.sound(target, shieldBlock());
        }
    }

    private static SoundEvent shieldBlock() {
        return SoundEvents.SHIELD_BLOCK.value();
    }

    private static SoundEvent shieldBreak() {
        return SoundEvents.SHIELD_BREAK.value();
    }

    private static List<Player> getNearPlayers(final WorldExplosion explosion) {
        return explosion.level().getEntitiesOfClass(Player.class, explosion.getBoundingBox(), player -> true);
    }

    private void sound(final LivingEntity entity, final SoundEvent soundEvent) {
        if (this.minecraft.level == null) {
            return;
        }
        this.minecraft.level.playSound(entity, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), 1F, 0.8F + this.minecraft.level.getRandom().nextFloat() * 0.4F);
    }

    public record WorldExplosion(Vec3 pos, Level level, float power) {
        public float getMaxDistance() {
            return this.power * 2;
        }

        public AABB getBoundingBox() {
            final float maxDistance = this.getMaxDistance();
            final int x1 = Mth.floor(this.pos.x - maxDistance - 1.0);
            final int x2 = Mth.floor(this.pos.x + maxDistance + 1.0);
            final int y1 = Mth.floor(this.pos.y - maxDistance - 1.0);
            final int y2 = Mth.floor(this.pos.y + maxDistance + 1.0);
            final int z1 = Mth.floor(this.pos.z - maxDistance - 1.0);
            final int z2 = Mth.floor(this.pos.z + maxDistance + 1.0);
            return new AABB(x1, y1, z1, x2, y2, z2);
        }
    }
}