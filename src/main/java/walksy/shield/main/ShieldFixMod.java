package walksy.shield.main;

import net.fabricmc.api.ModInitializer;
import walksy.shield.manager.ClientCommandManager;
import walksy.shield.manager.ConfigManager;

public class ShieldFixMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigManager.INSTANCE.loadConfig();
        ClientCommandManager.INSTANCE.initCommand();
    }
}
