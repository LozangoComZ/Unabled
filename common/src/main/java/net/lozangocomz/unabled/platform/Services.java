package net.lozangocomz.unabled.platform;

import net.lozangocomz.unabled.Constants;
import net.lozangocomz.unabled.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public final class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    private Services() {
    }

    private static <T> T load(Class<T> type) {
        T service = ServiceLoader.load(type).findFirst()
                .orElseThrow(() -> new IllegalStateException("No platform service found for " + type.getName()));
        Constants.LOG.debug("Loaded platform service {}", service.getClass().getName());
        return service;
    }
}