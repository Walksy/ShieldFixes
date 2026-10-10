package walksy.shieldfixes.mixin;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shieldfixes.ShieldFixes;
import walksy.shieldfixes.config.ShieldFixesConfig;
import walksy.shieldfixes.manager.EnvironmentEffectShieldManager;

@Mixin(Explosion.class)
public abstract class ServerExplosionMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private float radius;

    @Shadow
    public abstract Vec3 center();

    @Inject(method = "finalizeExplosion(Z)V", at = @At("HEAD"))
    public void onExplosion(final CallbackInfo ci) {
        if (!ShieldFixesConfig.modEnabled) {
            return;
        }
        ShieldFixes.getEnvironmentEffectShieldManager().onExplosion(new EnvironmentEffectShieldManager.WorldExplosion(this.center(), this.level, this.radius));
    }
}