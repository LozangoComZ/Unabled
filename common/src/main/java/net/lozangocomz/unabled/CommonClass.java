package net.lozangocomz.unabled;

import net.lozangocomz.unabled.platform.Services;

public final class CommonClass {
    private CommonClass() {
    }

    public static void init() {
        Constants.LOG.info("{} initialized on {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());
    }
}