package walksy.shield;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;

public class ShieldBlockMod implements ModInitializer {

    private static PlayerShieldingManager shieldingManager;

	@Override
	public void onInitialize() {
        shieldingManager = new PlayerShieldingManager();
	}

    public static PlayerShieldingManager getShieldingManager()
    {
        return shieldingManager;
    }
}
