package com.tristankechlo.healthcommand.commands;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthCommand {

	private static final UUID UUID = Mth.createInsecureUUID(ThreadLocalRandom.current());
	private static final String PREFIX = "commands.healthcommand.health";
	private static final Supplier<String> SUPPLIER = () -> {
		return HealthCommandMain.MOD_ID + ":" + HealthCommand.class.getSimpleName();
	};

	public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("health").requires((player) -> {
			int level = HealthCommandConfig.SERVER.permissionLevel.get();
			return player.hasPermission(level);
		}).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
				.then(Commands.argument("amount", IntegerArgumentType.integer()).executes((source) -> {
					return addHealth(source.getSource(), EntityArgument.getEntities(source, "targets"),
							IntegerArgumentType.getInteger(source, "amount"));
				})))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
						.then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((source) -> {
							return setHealth(source.getSource(), EntityArgument.getEntities(source, "targets"),
									IntegerArgumentType.getInteger(source, "amount"));
						}))))
				.then(Commands.literal("get")
						.then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> {
							return getHealth(source.getSource(), EntityArgument.getEntities(source, "targets"));
						}))));
		HealthCommandMain.LOGGER.debug(HealthCommandMain.MOD_ID + ": Health command registered");
	}

	private static int addHealth(CommandSourceStack source, Collection<? extends Entity> targets, int amount) {
		int i = 0;
		LivingEntity lastModified = null;
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			if (entity instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer) entity;
				if (player.isCreative() || player.isSpectator()) {
					continue;
				}
			}
			LivingEntity livingEntity = (LivingEntity) entity;
			lastModified = livingEntity;
			if (setHealthSingle(livingEntity, livingEntity.getHealth() + amount,
					() -> HealthCommandConfig.SERVER.goBeyondMaxHealthForAdding.get())) {
				i++;
			}
		}

		// send response
		if (i == 0) {
			source.sendFailure(new TranslatableComponent(PREFIX + ".no_entity_found"));
		} else if (i == 1 && lastModified != null) {
			source.sendSuccess(new TranslatableComponent(PREFIX + ".new_health", lastModified.getName().getString(),
					lastModified.getHealth()), false);
		} else {
			source.sendSuccess(new TranslatableComponent(PREFIX + ".add_health_multi", amount, i), false);
		}
		return i;
	}

	private static int setHealth(CommandSourceStack source, Collection<? extends Entity> targets, int amount) {
		int i = 0;
		LivingEntity lastModified = null;
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			if (entity instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer) entity;
				if (player.isCreative() || player.isSpectator()) {
					continue;
				}
			}
			LivingEntity livingEntity = (LivingEntity) entity;
			lastModified = livingEntity;
			if (setHealthSingle(livingEntity, amount,
					() -> HealthCommandConfig.SERVER.goBeyondMaxHealthForSetting.get())) {
				i++;
			}
		}

		// send response
		if (i == 0) {
			source.sendFailure(new TranslatableComponent(PREFIX + ".no_entity_found"));
		} else if (i == 1 && lastModified != null) {
			source.sendSuccess(new TranslatableComponent(PREFIX + ".new_health", lastModified.getName().getString(),
					lastModified.getHealth()), false);
		} else {
			source.sendSuccess(new TranslatableComponent(PREFIX + ".new_health_multi", i, amount), false);
		}
		return i;
	}

	private static int getHealth(CommandSourceStack source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity livingEntity = (LivingEntity) entity;
			float health = livingEntity.getHealth();
			source.sendSuccess(
					new TranslatableComponent(PREFIX + ".get_health", livingEntity.getName().getString(), health),
					false);
		}
		return 1;
	}

	private static boolean setHealthSingle(LivingEntity livingEntity, float newHealth,
			Supplier<Boolean> goBeyondMaxHealth) {
		AttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
		if (newHealth <= livingEntity.getMaxHealth()) {
			// no need for new maximum health here
			livingEntity.setHealth(newHealth);
			// decrease old modifier
			attribute.removeModifier(UUID);
			final double amount = newHealth - attribute.getBaseValue();
			attribute.addPermanentModifier(new AttributeModifier(UUID, SUPPLIER, amount, Operation.ADDITION));
			// remove old modifier
			if (livingEntity.getHealth() <= attribute.getBaseValue()) {
				attribute.removeModifier(UUID);
			}
		} else {
			boolean increaseBeyond = goBeyondMaxHealth.get();
			if (increaseBeyond) {
				// remove old attribute
				attribute.removeModifier(UUID);
				// increase maximum health of the entity
				final double amount = newHealth - attribute.getBaseValue();
				attribute.addPermanentModifier(new AttributeModifier(UUID, SUPPLIER, amount, Operation.ADDITION));
				// set new health
				livingEntity.setHealth(newHealth);
			} else {
				// only increase health to the maximum health
				livingEntity.setHealth(livingEntity.getMaxHealth());
			}
		}
		return true;
	}

}
