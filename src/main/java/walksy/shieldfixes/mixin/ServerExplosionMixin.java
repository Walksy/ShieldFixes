package walksy.shieldfixes.mixin;

//? if >=1.21.2 {
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerExplosion;
//?} else
//import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shieldfixes.ShieldFixes;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.manager.EnvironmentEffectShieldManager;

//? if >=1.21.2 {
@Mixin(ServerExplosion.class)
//?} else
//@Mixin(Explosion.class)
public abstract class ServerExplosionMixin {


    @Shadow
    public abstract Vec3 center();

    @Shadow
    @Final
    //? if >=1.21.2 {
    private ServerLevel level;
    //?} else
    //private Level level;

    @Shadow
    @Final
    private float radius;

    //? if >=1.21.2 {
    @Inject(method = "hurtEntities", at = @At("HEAD"))
    //?} else
    //@Inject(method = "finalizeExplosion(Z)V", at = @At("HEAD"))
    public void onExplosion(CallbackInfo ci) {
        if (!Config.modEnabled) return;
        EnvironmentEffectShieldManager.WorldExplosion explosion = new EnvironmentEffectShieldManager.WorldExplosion(this.center(), this.level, this.radius);
        ShieldFixes.getEnvironmentEffectShieldManager().onExplosion(explosion);
    }
}
