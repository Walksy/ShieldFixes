package walksy.shield.manager;

import net.minecraft.entity.LivingEntity;
import walksy.shield.main.ShieldBlockMod;

public class ShieldingPlayer
{
    public int shieldingTicks;
    private LivingEntity player;
    public ShieldingPlayer(LivingEntity player)
    {
        this.player = player;
        shieldingTicks = 0;
    }

    public LivingEntity getPlayer()
    {
        return player;
    }

    public void tick()
    {
        shieldingTicks++;
    }

    public boolean actuallyShielding()
    {
        return shieldingTicks >= 5;
    }
}
