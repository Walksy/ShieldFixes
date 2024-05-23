package walksy.shield.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import walksy.shield.main.ILivingEntity;
import walksy.shield.manager.PlayerShieldingManager;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ILivingEntity {
    
    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    @Override
    public void setActiveItem(ItemStack stack) {
        this.activeItemStack = stack;
    }

    @Override
    public void setItemUseTime(int time) {
        this.itemUseTimeLeft = time;
    }

    @Inject(method = "handleStatus", at = @At("HEAD"))
    public void onByteStatus(byte status, CallbackInfo ci)
    {
        PlayerShieldingManager.INSTANCE.handleByteStatus(status, this);
    }
}
