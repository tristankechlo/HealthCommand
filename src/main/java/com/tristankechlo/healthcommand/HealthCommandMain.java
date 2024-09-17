package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HealthCommandConfig.spec);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void register(final FMLServerStartingEvent event) {
        HealthCommand.register(event.getCommandDispatcher());
        ModCommand.register(event.getCommandDispatcher());
    }

}