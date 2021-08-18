package com.tristankechlo.healthcommand.commands;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class HealthCommand {

	private static final UUID UUID = java.util.UUID.fromString("cd4b25c8-660c-499f-a06f-2a818257c121");
	private static final String PREFIX = "commands.healthcommand.health";
	private static final Supplier<String> SUPPLIER = () -> {
		return HealthCommandMain.MOD_ID + ":" + HealthCommand.class.getSimpleName();
	};

	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("health").requires((player) -> {
			int level = HealthCommandConfig.SERVER.permissionLevel.get();
			return player.hasPermissionLevel(level);
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
						})))
				.then(Commands.literal("reset")
						.then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> {
							return resetHealth(source.getSource(), EntityArgument.getEntities(source, "targets"));
						}))));
		HealthCommandMain.LOGGER.debug(HealthCommandMain.MOD_ID + ": Health command registered");
	}

	private static int addHealth(CommandSource source, Collection<? extends Entity> targets, int amount) {
		int i = 0;
		LivingEntity lastModified = null;
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
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
			source.sendErrorMessage(new TranslationTextComponent(PREFIX + ".no_entity_found"));
		} else if (i == 1 && lastModified != null) {
			source.sendFeedback(new TranslationTextComponent(PREFIX + ".new_health", lastModified.getName().getString(),
					lastModified.getHealth()), false);
		} else {
			source.sendFeedback(new TranslationTextComponent(PREFIX + ".add_health_multi", amount, i), false);
		}
		return i;
	}

	private static int setHealth(CommandSource source, Collection<? extends Entity> targets, int amount) {
		int i = 0;
		LivingEntity lastModified = null;
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
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
			source.sendErrorMessage(new TranslationTextComponent(PREFIX + ".no_entity_found"));
		} else if (i == 1 && lastModified != null) {
			source.sendFeedback(new TranslationTextComponent(PREFIX + ".new_health", lastModified.getName().getString(),
					lastModified.getHealth()), false);
		} else {
			source.sendFeedback(new TranslationTextComponent(PREFIX + ".new_health_multi", i, amount), false);
		}
		return i;
	}

	private static int getHealth(CommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity livingEntity = (LivingEntity) entity;
			float health = livingEntity.getHealth();
			source.sendFeedback(
					new TranslationTextComponent(PREFIX + ".get_health", livingEntity.getName().getString(), health),
					false);
		}
		return 1;
	}

	private static int resetHealth(CommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity livingEntity = (LivingEntity) entity;
			IAttributeInstance attribute = livingEntity.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
			attribute.removeModifier(UUID);
			final float health = livingEntity.getHealth();
			livingEntity.setHealth(health);
			source.sendFeedback(
					new TranslationTextComponent(PREFIX + ".reset_health", livingEntity.getName().getString()), false);
		}
		return 1;
	}

	private static boolean setHealthSingle(LivingEntity livingEntity, float newHealth,
			Supplier<Boolean> goBeyondMaxHealth) {
		IAttributeInstance attribute = livingEntity.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
		if (newHealth <= livingEntity.getMaxHealth()) {
			// no need for new maximum health here
			livingEntity.setHealth(newHealth);
			// decrease old modifier
			attribute.removeModifier(UUID);
			final double amount = newHealth - attribute.getBaseValue();
			attribute.applyModifier(new AttributeModifier(UUID, SUPPLIER, amount, Operation.ADDITION));
		} else {
			boolean increaseBeyond = goBeyondMaxHealth.get();
			if (increaseBeyond) {
				// remove old attribute
				attribute.removeModifier(UUID);
				// increase maximum health of the entity
				final double amount = newHealth - attribute.getBaseValue();
				attribute.applyModifier(new AttributeModifier(UUID, SUPPLIER, amount, Operation.ADDITION));
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
