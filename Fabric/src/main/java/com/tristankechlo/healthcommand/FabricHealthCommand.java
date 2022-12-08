package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricHealthCommand implements ModInitializer {

    @Override
    public void onInitialize() {
        // setup configs
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadAndVerifyConfig();
        });

        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommand.register(dispatcher);
            HealthCommand.register(dispatcher);
        });
    }

}
