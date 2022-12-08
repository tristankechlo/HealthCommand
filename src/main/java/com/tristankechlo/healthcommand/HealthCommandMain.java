package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HealthCommandMain implements ModInitializer {

    public static final String MOD_NAME = "HealthCommand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final String MOD_ID = "healthcommand";
    public static final String GITHUB_URL = "https://github.com/tristankechlo/HealthCommand";
    public static final String GITHUB_ISSUE_URL = GITHUB_URL + "/issues";
    public static final String DISCORD_URL = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE_URL = "https://curseforge.com/minecraft/mc-mods/health-command";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/health-command";

    @Override
    public void onInitialize() {
        // setup configs
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            ConfigManager.loadAndVerifyConfig();
        });

        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> {
            ModCommand.register(dispatcher);
            HealthCommand.register(dispatcher);
        });
    }

}
