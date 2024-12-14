package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod("healthcommand")
public class NeoforgeHealthCommand {

    public NeoforgeHealthCommand() {
        // register commands
        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        // setup configs
        NeoForge.EVENT_BUS.addListener(this::commonSetup);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        ModCommand.register(event.getDispatcher());
        HealthCommand.register(event.getDispatcher());
    }

    private void commonSetup(final ServerAboutToStartEvent event) {
        ConfigManager.loadAndVerifyConfig(FMLPaths.CONFIGDIR.get());
    }

}
