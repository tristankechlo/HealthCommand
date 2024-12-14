package com.tristankechlo.healthcommand;

import com.google.auto.service.AutoService;
import com.tristankechlo.healthcommand.platform.IPlatformHelper;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

@AutoService(IPlatformHelper.class)
public class NeoforgePlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

}
