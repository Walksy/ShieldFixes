package walksy.shieldfixes;

import net.fabricmc.api.ModInitializer;
import walksy.shieldfixes.manager.EnvironmentEffectShieldManager;


public class ShieldFixes implements ModInitializer {

    private static EnvironmentEffectShieldManager environmentEffectShieldManager;

    @Override
    public void onInitialize() {
        environmentEffectShieldManager = new EnvironmentEffectShieldManager();
    }

    public static EnvironmentEffectShieldManager getEnvironmentEffectShieldManager() {
        return environmentEffectShieldManager;
    }
}
