package walksy.shieldfixes.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shieldfixes.ShieldFixes;
import walksy.shieldfixes.config.Config;
import walksy.shieldfixes.interfaze.ILivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {

    @Shadow
    protected ItemStack useItem;

    @Shadow
    protected int useItemRemaining;

    @Override
    public void setActiveItem(ItemStack stack) {
        this.useItem = stack;
    }

    @Override
    public void setItemUseTime(int time) {
        this.useItemRemaining = time;
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    public void onByteStatus(byte status, CallbackInfo ci) {
        if (!Config.modEnabled) return;
        if (status == 30) {
            ShieldFixes.getEnvironmentEffectShieldManager().onDisable(LivingEntity.class.cast(this));
        }
    }
}
