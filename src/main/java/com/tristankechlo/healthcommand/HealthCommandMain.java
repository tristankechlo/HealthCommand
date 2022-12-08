package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(HealthCommandMain.MOD_ID)
public class HealthCommandMain {

    public static final String MOD_NAME = "HealthCommand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final String MOD_ID = "healthcommand";
    public static final String GITHUB_URL = "https://github.com/tristankechlo/HealthCommand";
    public static final String GITHUB_ISSUE_URL = GITHUB_URL + "/issues";
    public static final String DISCORD_URL = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE_URL = "https://curseforge.com/minecraft/mc-mods/health-command";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/health-command";

    public HealthCommandMain() {
        // register commands
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        // setup configs
        MinecraftForge.EVENT_BUS.addListener(this::commonSetup);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        ModCommand.register(event.getDispatcher());
        HealthCommand.register(event.getDispatcher());
    }

    private void commonSetup(final FMLServerAboutToStartEvent event) {
        ConfigManager.loadAndVerifyConfig();
    }

}