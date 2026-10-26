package net.lozangocomz.unabled.config;

import net.fabricmc.loader.api.FabricLoader;
import net.lozangocomz.unabled.Config;
import net.lozangocomz.unabled.Constants;
import net.lozangocomz.unabled.Settings;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class FabricConfig {
    private FabricConfig() {
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("unabled.properties");
        Properties properties = new Properties();
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            } else {
                saveDefaults(path, properties);
            }
        } catch (IOException exception) {
            Constants.LOG.error("Could not read Unabled configuration at {}", path, exception);
        }

        Settings defaults = Settings.defaults();
        Config.set(new Settings(
                integer(properties, "damage_mode", defaults.damageMode()),
                decimal(properties, "damage_player", defaults.damagePlayer()),
                integer(properties, "unequipped_mode", defaults.unequippedMode()),
                integer(properties, "dig_tools_mode", defaults.digToolsMode()),
                integer(properties, "required_tools_mode", defaults.requiredToolsMode()),
                decimal(properties, "breaking_speed_threshold", defaults.breakingSpeedThreshold()),
                strings(properties.getProperty("ignored_ids", ""))
        ));
    }

    private static int integer(Properties properties, String name, int fallback) {
        try {
            int value = Integer.parseInt(properties.getProperty(name, Integer.toString(fallback)).trim());
            return Math.max(1, Math.min(3, value));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double decimal(Properties properties, String name, double fallback) {
        try {
            double value = Double.parseDouble(properties.getProperty(name, Double.toString(fallback)).trim());
            return Double.isFinite(value) ? Math.max(0.0, Math.min(20.0, value)) : fallback;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static List<String> strings(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .toList();
    }

    private static void saveDefaults(Path path, Properties properties) throws IOException {
        properties.setProperty("damage_mode", "2");
        properties.setProperty("damage_player", "0.5");
        properties.setProperty("unequipped_mode", "3");
        properties.setProperty("dig_tools_mode", "2");
        properties.setProperty("required_tools_mode", "1");
        properties.setProperty("breaking_speed_threshold", "1.25");
        properties.setProperty("ignored_ids", "");
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            properties.store(writer, "unabled block breaking rules");
        }
    }
}