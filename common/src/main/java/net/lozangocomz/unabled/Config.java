package net.lozangocomz.unabled;

public final class Config {
    private static volatile Settings settings = Settings.defaults();

    private Config() {
    }

    public static Settings get() {
        return settings;
    }

    public static void set(Settings newSettings) {
        settings = newSettings;
    }
}