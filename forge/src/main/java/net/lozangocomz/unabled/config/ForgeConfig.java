package net.lozangocomz.unabled.config;

import net.lozangocomz.unabled.Config;
import net.lozangocomz.unabled.Settings;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ForgeConfig {
    private static final AtomicBoolean LOADED = new AtomicBoolean();
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue DAMAGE_MODE = BUILDER
            .comment("When to damage the player: 1 = disabled, 2 = without any item, 3 = without any digging tool.")
            .translation("unabled.config.damage_mode")
            .defineInRange("damage_mode", 2, 1, 3);
    private static final ForgeConfigSpec.DoubleValue DAMAGE_PLAYER = BUILDER
            .comment("Damage dealt to the player when the selected condition is met.")
            .translation("unabled.config.damage_player")
            .defineInRange("damage_player", 0.5, 0.0, 20.0);
    private static final ForgeConfigSpec.IntValue UNEQUIPPED_MODE = BUILDER
            .comment("Breaking without tools: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.unequipped_mode")
            .defineInRange("unequipped_mode", 3, 1, 3);
    private static final ForgeConfigSpec.IntValue DIG_TOOLS_MODE = BUILDER
            .comment("Breaking with an unsuitable digging tool: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.dig_tools_mode")
            .defineInRange("dig_tools_mode", 2, 1, 3);
    private static final ForgeConfigSpec.IntValue REQUIRED_TOOLS_MODE = BUILDER
            .comment("Breaking with the required tool: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.required_tools_mode")
            .defineInRange("required_tools_mode", 1, 1, 3);
    private static final ForgeConfigSpec.DoubleValue BREAKING_SPEED_THRESHOLD = BUILDER
            .comment("Blocks with a destroy speed below this value are not affected.")
            .translation("unabled.config.breaking_speed_threshold")
            .defineInRange("breaking_speed_threshold", 1.25, 0.0, 20.0);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> IGNORED_IDS = BUILDER
            .comment("Ignored block IDs or tags, such as minecraft:oak_log or #minecraft:logs.")
            .translation("unabled.config.ignored_id")
            .defineListAllowEmpty("ignored_ids", List.of(), value -> value instanceof String);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ForgeConfig() {
    }

    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SPEC);
        context.getModEventBus().addListener(ForgeConfig::onLoading);
        context.getModEventBus().addListener(ForgeConfig::onReloading);
        context.getModEventBus().addListener(ForgeConfig::onUnloading);
    }

    private static void onLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            LOADED.set(true);
            updateCommonConfig();
        }
    }

    private static void onReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            LOADED.set(true);
            updateCommonConfig();
        }
    }

    private static void onUnloading(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            LOADED.set(false);
        }
    }

    private static void updateCommonConfig() {
        Config.set(new Settings(
                DAMAGE_MODE.get(), DAMAGE_PLAYER.get(), UNEQUIPPED_MODE.get(), DIG_TOOLS_MODE.get(),
                REQUIRED_TOOLS_MODE.get(), BREAKING_SPEED_THRESHOLD.get(),
                IGNORED_IDS.get().stream().map(String::valueOf).toList()
        ));
    }

    public static boolean isLoaded() {
        return LOADED.get();
    }

    public static void save(Settings settings) {
        if (!isLoaded()) {
            throw new IllegalStateException("The server config is not loaded");
        }
        DAMAGE_MODE.set(settings.damageMode());
        DAMAGE_PLAYER.set(settings.damagePlayer());
        UNEQUIPPED_MODE.set(settings.unequippedMode());
        DIG_TOOLS_MODE.set(settings.digToolsMode());
        REQUIRED_TOOLS_MODE.set(settings.requiredToolsMode());
        BREAKING_SPEED_THRESHOLD.set(settings.breakingSpeedThreshold());
        IGNORED_IDS.set(List.copyOf(settings.ignoredIds()));
        SPEC.save();
        updateCommonConfig();
    }
}
