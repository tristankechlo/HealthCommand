package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HealthCommand {

    private static final UUID UUID = java.util.UUID.fromString("cd4b25c8-660c-499f-a06f-2a818257c121");
    private static final String PREFIX = "commands.healthcommand.health";
    private static final Supplier<String> SUPPLIER = () -> {
        return HealthCommandMain.MOD_ID + ":" + HealthCommand.class.getSimpleName();
    };

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("health").requires((player) -> {
                    int level = HealthCommandConfig.permissionLevel.get();
                    return player.hasPermissionLevel(level);
                }).then(literal("add").then(argument("targets", EntityArgumentType.entities())
                        .then(argument("amount", IntegerArgumentType.integer()).executes((source) -> {
                            return addHealth(source.getSource(), EntityArgumentType.getEntities(source, "targets"),
                                    IntegerArgumentType.getInteger(source, "amount"));
                        })))).then(literal("set").then(argument("targets", EntityArgumentType.entities())
                        .then(argument("amount", IntegerArgumentType.integer(0)).executes((source) -> {
                            return setHealth(source.getSource(), EntityArgumentType.getEntities(source, "targets"),
                                    IntegerArgumentType.getInteger(source, "amount"));
                        }))))
                .then(literal("get")
                        .then(argument("targets", EntityArgumentType.entities()).executes((source) -> {
                            return getHealth(source.getSource(), EntityArgumentType.getEntities(source, "targets"));
                        })))
                .then(literal("reset")
                        .then(argument("targets", EntityArgumentType.entities()).executes((source) -> {
                            return resetHealth(source.getSource(), EntityArgumentType.getEntities(source, "targets"));
                        }))));
        HealthCommandMain.LOGGER.debug(HealthCommandMain.MOD_ID + ": Health command registered");
    }

    private static int addHealth(ServerCommandSource source, Collection<? extends Entity> targets, int amount) {
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
            if (setHealthSingle(livingEntity, livingEntity.getHealth() + amount, HealthCommandConfig.goBeyondMaxHealthForAdding::get)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendError(new TranslatableText(PREFIX + ".no_entity_found"));
        } else if (i == 1 && lastModified != null) {
            source.sendFeedback(new TranslatableText(PREFIX + ".new_health", lastModified.getName().getString(), lastModified.getHealth()), false);
        } else {
            source.sendFeedback(new TranslatableText(PREFIX + ".add_health_multi", amount, i), false);
        }
        return i;
    }

    private static int setHealth(ServerCommandSource source, Collection<? extends Entity> targets, int amount) {
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
            if (setHealthSingle(livingEntity, amount, HealthCommandConfig.goBeyondMaxHealthForSetting::get)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendError(new TranslatableText(PREFIX + ".no_entity_found"));
        } else if (i == 1 && lastModified != null) {
            source.sendFeedback(new TranslatableText(PREFIX + ".new_health", lastModified.getName().getString(), lastModified.getHealth()), false);
        } else {
            source.sendFeedback(new TranslatableText(PREFIX + ".new_health_multi", i, amount), false);
        }
        return i;
    }

    private static int getHealth(ServerCommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            float health = livingEntity.getHealth();
            source.sendFeedback(new TranslatableText(PREFIX + ".get_health", livingEntity.getName().getString(), health), false);
        }
        return 1;
    }

    private static int resetHealth(ServerCommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            EntityAttributeInstance attribute = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            attribute.removeModifier(UUID);
            final float health = livingEntity.getHealth();
            livingEntity.setHealth(health);
            source.sendFeedback(new TranslatableText(PREFIX + ".reset_health", livingEntity.getName().getString()), false);
        }
        return 1;
    }

    private static boolean setHealthSingle(LivingEntity livingEntity, float newHealth, Supplier<Boolean> goBeyondMaxHealth) {
        EntityAttributeInstance attribute = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (newHealth <= livingEntity.getMaxHealth()) {
            // no need for new maximum health here
            livingEntity.setHealth(newHealth);
            // decrease old modifier
            attribute.removeModifier(UUID);
            final double amount = newHealth - attribute.getBaseValue();
            attribute.addPersistentModifier(new EntityAttributeModifier(UUID, SUPPLIER, amount, EntityAttributeModifier.Operation.ADDITION));
        } else {
            boolean increaseBeyond = goBeyondMaxHealth.get();
            if (increaseBeyond) {
                // remove old attribute
                attribute.removeModifier(UUID);
                // increase maximum health of the entity
                final double amount = newHealth - attribute.getBaseValue();
                attribute.addPersistentModifier(new EntityAttributeModifier(UUID, SUPPLIER, amount, EntityAttributeModifier.Operation.ADDITION));
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
