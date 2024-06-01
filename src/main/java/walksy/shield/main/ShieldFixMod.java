package walksy.shield.main;

import net.fabricmc.api.ModInitializer;
import walksy.shield.manager.ClientCommandManager;
import walksy.shield.manager.ConfigManager;
import walksy.shield.manager.PlayerShieldingManager;

public class ShieldFixMod implements ModInitializer {
    private static PlayerShieldingManager shieldingManager;

    @Override
    public void onInitialize() {
        ConfigManager.INSTANCE.loadConfig();
        ClientCommandManager.INSTANCE.initCommand();
        shieldingManager = new PlayerShieldingManager();
    }

    public static PlayerShieldingManager getShieldingManager()
    {
        return shieldingManager;
    }
}
