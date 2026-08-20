package walksy.shieldfixes.config;

import main.walksy.lib.api.WalksyLibApi;
import main.walksy.lib.api.WalksyLibConfig;
//? if >=26.1 {
import main.walksy.lib.core.config.impl.ModConfig;
//?} else {
/*import main.walksy.lib.core.config.impl.LocalConfig;
*///?}

public class WalksyLibIntegration implements WalksyLibApi {

    @Override
    //? if >=26.1 {
    public ModConfig getConfig() {
    //?} else {
    /*public LocalConfig getConfig() {
    *///?}
        WalksyLibConfig config = new Config();
        return config.getOrCreateConfig();
    }
}
