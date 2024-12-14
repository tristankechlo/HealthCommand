package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.healthcommand.config.ConfigManager;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.function.Supplier;

public class HealthCommand {

    private static final ResourceLocation ATTRIBUTE_ID = ResourceLocation.fromNamespaceAndPath("healthcommand", "main");

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("health").requires((player) -> {
                    int level = HealthCommandConfig.get().permissionLevel();
                    return player.hasPermission(level);
                }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((source) -> {
                            return addHealth(source.getSource(), EntityArgument.getEntities(source, "targets"), IntegerArgumentType.getInteger(source, "amount"));
                        })))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((source) -> {
                            return setHealth(source.getSource(), EntityArgument.getEntities(source, "targets"), IntegerArgumentType.getInteger(source, "amount"));
                        }))))
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> {
                            return getHealth(source.getSource(), EntityArgument.getEntities(source, "targets"));
                        })))
                .then(Commands.literal("reset")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes((source) -> {
                            return resetHealth(source.getSource(), EntityArgument.getEntities(source, "targets"));
                        }))));
        ConfigManager.LOGGER.info("Command '/health' registered");
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
            if (setHealthSingle(livingEntity, livingEntity.getHealth() + amount, HealthCommandConfig.get()::goBeyondMaxHealthForAdding)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendFailure(Component.literal("There were no entities with a health attribute in your selection."));
        } else if (i == 1 && lastModified != null) {
            final String m = "New health of " + lastModified.getName().getString() + " is " + lastModified.getHealth();
            source.sendSuccess(() -> Component.literal(m), false);
        } else {
            final String m = "Health of " + i + " entities is increased by " + amount;
            source.sendSuccess(() -> Component.literal(m), false);
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
            if (setHealthSingle(livingEntity, amount, HealthCommandConfig.get()::goBeyondMaxHealthForSetting)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendFailure(Component.literal("There were no entities with a health attribute in your selection."));
        } else if (i == 1 && lastModified != null) {
            final String m = "New health of " + lastModified.getName().getString() + " is " + lastModified.getHealth();
            source.sendSuccess(() -> Component.literal(m), false);
        } else {
            final String m = "New health of " + i + " entities is now " + amount;
            source.sendSuccess(() -> Component.literal(m), false);
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
            final String m = livingEntity.getName().getString() + " has " + health + " health left.";
            source.sendSuccess(() -> Component.literal(m), false);
        }
        return 1;
    }

    private static int resetHealth(CommandSourceStack source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            AttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            attribute.removeModifier(ATTRIBUTE_ID);
            final float health = livingEntity.getHealth();
            livingEntity.setHealth(health);
            final String m = "Resetted the health of " + livingEntity.getName().getString();
            source.sendSuccess(() -> Component.literal(m), false);
        }
        return 1;
    }

    private static boolean setHealthSingle(LivingEntity livingEntity, float newHealth, Supplier<Boolean> goBeyondMaxHealth) {
        AttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (newHealth <= livingEntity.getMaxHealth()) {
            // no need for new maximum health here
            livingEntity.setHealth(newHealth);
            // decrease old modifier
            attribute.removeModifier(ATTRIBUTE_ID);
            final double amount = newHealth - attribute.getBaseValue();
            attribute.addPermanentModifier(new AttributeModifier(ATTRIBUTE_ID, amount, Operation.ADD_VALUE));
        } else {
            boolean increaseBeyond = goBeyondMaxHealth.get();
            if (increaseBeyond) {
                // remove old attribute
                attribute.removeModifier(ATTRIBUTE_ID);
                // increase maximum health of the entity
                final double amount = newHealth - attribute.getBaseValue();
                attribute.addPermanentModifier(new AttributeModifier(ATTRIBUTE_ID, amount, Operation.ADD_VALUE));
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
