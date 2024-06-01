package walksy.shield.manager;

import net.minecraft.entity.LivingEntity;
import walksy.shield.main.ShieldFixMod;

public class ShieldingPlayer
{
    public int shieldingTicks, itemUseTimeLeft;
    private LivingEntity player;

    public ShieldingPlayer(LivingEntity player)
    {
        this.player = player;
        shieldingTicks = 0;
        itemUseTimeLeft = 72000;
    }

    public LivingEntity getPlayer()
    {
        return player;
    }

    public void tick()
    {
        itemUseTimeLeft--;
        shieldingTicks++;
    }

    public boolean actuallyShielding()
    {
        return ConfigManager.INSTANCE.factor5TickDelay ? shieldingTicks >= 5 && !ShieldFixMod.getShieldingManager().isHoldingAnimationItemMainHand(this.player) : shieldingTicks >= 0 && !ShieldFixMod.getShieldingManager().isHoldingAnimationItemMainHand(this.player);
    }
}
