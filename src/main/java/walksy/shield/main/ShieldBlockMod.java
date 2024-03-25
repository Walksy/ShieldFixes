package walksy.shield.main;

import net.fabricmc.api.ModInitializer;
import walksy.shield.manager.PlayerShieldingManager;

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
