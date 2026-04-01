package walksy.shieldfixes.config;

import main.walksy.lib.api.WalksyLibConfig;
import main.walksy.lib.core.config.impl.LocalConfig;
import main.walksy.lib.core.config.local.Category;
import main.walksy.lib.core.config.local.Option;
import main.walksy.lib.core.config.local.OptionDescription;
import main.walksy.lib.core.config.local.options.BooleanOption;
import main.walksy.lib.core.config.local.options.NumericalOption;
import main.walksy.lib.core.config.local.options.PixelGridAnimationOption;
import main.walksy.lib.core.config.local.options.StringListOption;
import main.walksy.lib.core.config.local.options.groups.OptionGroup;
import main.walksy.lib.core.config.local.options.type.PixelGrid;
import main.walksy.lib.core.config.local.options.type.PixelGridAnimation;
import main.walksy.lib.core.utils.PathUtils;
import walksy.shieldfixes.ShieldFixes;

import java.util.List;

public class Config implements WalksyLibConfig {

    public static boolean modEnabled = true;
    public static boolean factorDelay = false;

    /**
     * General Category
     */

    private final Option<Boolean> modEnabledOption = BooleanOption.createBuilder("Mod Enabled", () -> modEnabled, modEnabled, newValue -> modEnabled = newValue)
        .description(OptionDescription.ofOrderedString(() -> "Should Shield Fixes be enabled"))
        .build();

    private final Option<Boolean> factorDelayOption = BooleanOption.createBuilder("Factor Delay", () -> factorDelay, factorDelay, newValue -> factorDelay = newValue)
        .description(OptionDescription.ofOrderedString(() -> "Should the 5 tick delay of shields be factored in for shield predicates"))
        .availability(() -> modEnabled, "'Mod Enabled' must be enabled")
        .build();


    private final Category generalCategory = Category.createBuilder("General")
        .group(OptionGroup.createBuilder("Global Options")
            .addOption(modEnabledOption)
            .addOption(factorDelayOption)
            .build())
        .build();


    @Override
    public LocalConfig define() {
        return LocalConfig.createBuilder("Shield Fixes")
            .path(PathUtils.ofConfigDir("shieldfixes"))
            .category(generalCategory)
            .build();
    }
}
