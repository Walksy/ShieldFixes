package walksy.shieldfixes.config;

import main.walksy.lib.api.WalksyLibApi;
import main.walksy.lib.api.config.Config;
import main.walksy.lib.api.config.option.Category;
import main.walksy.lib.api.config.option.OptionGroup;
import main.walksy.lib.api.config.option.Options;
import main.walksy.lib.api.config.PathUtils;

public final class ShieldFixesConfig implements WalksyLibApi {
    public static boolean modEnabled = true;
    public static boolean factorDelay = false;

    @Override
    public Config define() {
        return Config.builder()
            .path(PathUtils.ofConfigDir("shieldfixes"))
            .category(Category.builder("General")
                .group(OptionGroup.builder("General")
                    .option(Options.ofBoolean("Mod Enabled", () -> modEnabled, true, value -> modEnabled = value).build())
                    .option(Options.ofBoolean("Factor Delay", () -> factorDelay, false, value -> factorDelay = value).description("Only count a shield as up after vanilla's short raise delay").build())
                    .build())
                .build())
            .build();
    }
}
