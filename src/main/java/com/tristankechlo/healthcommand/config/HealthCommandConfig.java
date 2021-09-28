package com.tristankechlo.healthcommand.config;

import com.tristankechlo.healthcommand.HealthCommandMain;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class HealthCommandConfig {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final Server SERVER = new Server(BUILDER);
	public static final ForgeConfigSpec spec = BUILDER.build();

	public static class Server {

		public final IntValue permissionLevel;
		public final BooleanValue goBeyondMaxHealthForAdding;
		public final BooleanValue goBeyondMaxHealthForSetting;

		Server(ForgeConfigSpec.Builder builder) {
			builder.comment("options for the command health").push("HealthCommand");
			permissionLevel = builder.comment("the permission level needed to execute this command").worldRestart()
					.defineInRange("permissionLevel", 2, 0, 4);
			goBeyondMaxHealthForAdding = builder.comment(
					"whether or not the health of the entity should increase beyond the maximum health for adding health")
					.worldRestart().define("goBeyondMaxHealthForAdding", true);
			goBeyondMaxHealthForSetting = builder.comment(
					"whether or not the health of the entity should increase beyond the maximum health for setting health")
					.worldRestart().define("goBeyondMaxHealthForSetting", true);
			builder.pop();
		}

	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		HealthCommandMain.LOGGER.debug("Loaded config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading configEvent) {
		HealthCommandMain.LOGGER.debug("Config just got changed on the file system!");
	}

}
