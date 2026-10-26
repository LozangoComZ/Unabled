package net.lozangocomz.unabled.config;

import net.lozangocomz.unabled.Config;
import net.lozangocomz.unabled.Settings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class NeoForgeConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue DAMAGE_MODE = BUILDER
            .comment("When to damage the player: 1 = disabled, 2 = without any item, 3 = without any digging tool.")
            .translation("unabled.config.damage_mode")
            .defineInRange("damage_mode", 2, 1, 3);
    private static final ModConfigSpec.DoubleValue DAMAGE_PLAYER = BUILDER
            .comment("Damage dealt to the player when the selected condition is met.")
            .translation("unabled.config.damage_player")
            .defineInRange("damage_player", 0.5, 0.0, 20.0);
    private static final ModConfigSpec.IntValue UNEQUIPPED_MODE = BUILDER
            .comment("Breaking without tools: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.unequipped_mode")
            .defineInRange("unequipped_mode", 3, 1, 3);
    private static final ModConfigSpec.IntValue DIG_TOOLS_MODE = BUILDER
            .comment("Breaking with an unsuitable digging tool: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.dig_tools_mode")
            .defineInRange("dig_tools_mode", 2, 1, 3);
    private static final ModConfigSpec.IntValue REQUIRED_TOOLS_MODE = BUILDER
            .comment("Breaking with the required tool: 1 = normal drops, 2 = no drops, 3 = never break.")
            .translation("unabled.config.required_tools_mode")
            .defineInRange("required_tools_mode", 1, 1, 3);
    private static final ModConfigSpec.DoubleValue BREAKING_SPEED_THRESHOLD = BUILDER
            .comment("Blocks with a destroy speed below this value are not affected.")
            .translation("unabled.config.breaking_speed_threshold")
            .defineInRange("breaking_speed_threshold", 1.25, 0.0, 20.0);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> IGNORED_IDS = BUILDER
            .comment("Ignored block IDs or tags, such as minecraft:oak_log or #minecraft:logs.")
            .translation("unabled.config.ignored_id")
            .defineListAllowEmpty("ignored_ids", List::of, () -> "", value -> value instanceof String);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private NeoForgeConfig() {
    }

    public static void register(ModContainer container, IEventBus modEventBus) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC);
        modEventBus.addListener(NeoForgeConfig::onLoading);
        modEventBus.addListener(NeoForgeConfig::onReloading);
    }

    private static void onLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            updateCommonConfig();
        }
    }

    private static void onReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            updateCommonConfig();
        }
    }

    private static void updateCommonConfig() {
        Config.set(new Settings(
                DAMAGE_MODE.get(), DAMAGE_PLAYER.get(), UNEQUIPPED_MODE.get(), DIG_TOOLS_MODE.get(),
                REQUIRED_TOOLS_MODE.get(), BREAKING_SPEED_THRESHOLD.get(),
                IGNORED_IDS.get().stream().map(String::valueOf).toList()
        ));
    }
}