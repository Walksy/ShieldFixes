package walksy.shieldfixes;

import net.fabricmc.api.ModInitializer;
import walksy.shieldfixes.manager.EnvironmentEffectShieldManager;
import walksy.shieldfixes.manager.ShieldStateManager;


public class ShieldFixes implements ModInitializer {

    private static ShieldStateManager shieldStateManager;
    private static EnvironmentEffectShieldManager environmentEffectShieldManager;

    @Override
    public void onInitialize() {
        shieldStateManager = new ShieldStateManager();
        environmentEffectShieldManager = new EnvironmentEffectShieldManager();
    }

    public static ShieldStateManager getShieldStateManager() {
        return shieldStateManager;
    }

    public static EnvironmentEffectShieldManager getEnvironmentEffectShieldManager() {
        return environmentEffectShieldManager;
    }
}
