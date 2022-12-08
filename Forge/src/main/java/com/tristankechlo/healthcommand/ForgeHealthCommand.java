package com.tristankechlo.healthcommand;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.commands.ModCommand;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

@Mod(HealthCommandMain.MOD_ID)
public class ForgeHealthCommand {

    public ForgeHealthCommand() {
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
