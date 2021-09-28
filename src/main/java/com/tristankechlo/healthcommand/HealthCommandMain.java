package com.tristankechlo.healthcommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tristankechlo.healthcommand.commands.HealthCommand;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(HealthCommandMain.MOD_ID)
public class HealthCommandMain {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "healthcommand";

	public HealthCommandMain() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HealthCommandConfig.spec);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void register(final RegisterCommandsEvent event) {
		HealthCommand.register(event.getDispatcher());
	}

}