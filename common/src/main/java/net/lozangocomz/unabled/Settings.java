package net.lozangocomz.unabled;

import java.util.List;

public record Settings(
        int damageMode,
        double damagePlayer,
        int unequippedMode,
        int digToolsMode,
        int requiredToolsMode,
        double breakingSpeedThreshold,
        List<String> ignoredIds
) {
    public Settings {
        ignoredIds = List.copyOf(ignoredIds);
    }

    public static Settings defaults() {
        return new Settings(2, 0.5, 3, 2, 1, 1.25, List.of());
    }
}