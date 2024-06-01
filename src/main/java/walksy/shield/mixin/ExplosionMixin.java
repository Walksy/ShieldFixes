package walksy.shield.mixin;

import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.main.ShieldFixMod;
import walksy.shield.manager.PlayerShieldingManager;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private double x;

    @Shadow
    @Final
    private double y;

    @Shadow
    @Final
    private float power;

    @Shadow
    @Final
    private World world;

    @Shadow
    @Final
    private double z;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    public void onExplosion(boolean particles, CallbackInfo ci)
    {
        PlayerShieldingManager.INSTANCE.onExplosion(this.x, this.y, this.z, this.power, this.world);
    }
}
