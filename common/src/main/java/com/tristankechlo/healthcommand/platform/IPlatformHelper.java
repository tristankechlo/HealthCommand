package com.tristankechlo.healthcommand.platform;

import com.tristankechlo.healthcommand.HealthCommandMain;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface IPlatformHelper {

    IPlatformHelper INSTANCE = load(IPlatformHelper.class);

    static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        HealthCommandMain.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    Path getConfigDirectory();

}
