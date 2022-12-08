package com.tristankechlo.healthcommand.platform;

import com.tristankechlo.healthcommand.HealthCommandMain;

import java.util.ServiceLoader;

public final class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        HealthCommandMain.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}