package walksy.shield;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerShieldingManager {

    public Map<LivingEntity, Integer> playersUsingShield = new HashMap<>();
    public CopyOnWriteArrayList<LivingEntity> playersShielding$Server = new CopyOnWriteArrayList<>();

    public void tick()
    {
        playersShielding$Server.forEach(playerShielding ->
        {
            if (!playerShielding.isUsingItem())
            {
                playersShielding$Server.remove(playerShielding);
            }
        });
        MinecraftClient.getInstance().world.getEntities().forEach(entity ->
        {
            if (entity instanceof LivingEntity player && player instanceof PlayerEntity) //mhm
            {
                if (usingShield(player) && !playersUsingShield.containsKey(player))
                {
                    playersUsingShield.put(player, 0);
                }
            }
        });
        playersUsingShield.forEach((key, value) ->
        {
            value++;
            if (value >= 5 && !playersShielding$Server.contains(key))
            {
                playersShielding$Server.add(key);
            }
        });
    }

    boolean usingShield(LivingEntity entity)
    {
        return entity.isUsingItem() && entity.getMainHandStack().isOf(Items.SHIELD);
    }
}
