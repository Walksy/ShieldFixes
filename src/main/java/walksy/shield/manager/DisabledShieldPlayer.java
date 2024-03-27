package walksy.shield.manager;

import net.minecraft.entity.LivingEntity;

public class DisabledShieldPlayer {
    public LivingEntity player;
    public int disabledTime;
    public DisabledShieldPlayer(LivingEntity entity)
    {
        this.player = entity;
        disabledTime = 100;
    }

    public void tick()
    {
        disabledTime--;
    }
}
