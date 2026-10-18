package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.healthcommand.config.ConfigManager;
import com.tristankechlo.healthcommand.config.HealthCommandConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;
import java.util.UUID;

public class HealthCommand {

    protected static final UUID UUID = java.util.UUID.fromString("cd4b25c8-660c-499f-a06f-2a818257c121");

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("health")
                .requires((player) -> player.hasPermission(HealthCommandConfig.get().permissionLevel()))
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg())
                                        .executes(HealthCommand::addHealth)
                                        .then(Commands.argument("updateMaxHealth", BoolArgumentType.bool())
                                                .executes((c) -> addHealth(c, BoolArgumentType.getBool(c, "updateMaxHealth")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("health", FloatArgumentType.floatArg(0))
                                        .executes(HealthCommand::setHealth)
                                        .then(Commands.argument("updateMaxHealth", BoolArgumentType.bool())
                                                .executes((c) -> setHealth(c, BoolArgumentType.getBool(c, "updateMaxHealth")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(HealthCommand::getHealth)
                        )
                )
                .then(Commands.literal("reset")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(HealthCommand::resetHealth)
                        )
                )
        );
        ConfigManager.LOGGER.info("Command '/health' registered");
    }

    private static int addHealth(CommandContext<CommandSource> context, boolean updateMaxHealth) throws CommandSyntaxException {
        final CommandSource source = context.getSource();
        final Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        final float amount = FloatArgumentType.getFloat(context, "amount");

        int i = 0;
        LivingEntity lastModified = null;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            if (CommandLogic.addHealthSingle(lastModified = (LivingEntity) entity, amount, updateMaxHealth)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendFailure(new StringTextComponent("There were no entities with a health attribute in your selection."));
        } else if (i == 1) {
            final String m = "New health of " + lastModified.getName().getString() + " is " + lastModified.getHealth();
            source.sendSuccess(new StringTextComponent(m), false);
        } else {
            final String m = "Health of " + i + " entities is increased by " + amount;
            source.sendSuccess(new StringTextComponent(m), false);
        }
        return i;
    }

    private static int setHealth(CommandContext<CommandSource> context, boolean updateMaxHealth) throws CommandSyntaxException {
        final CommandSource source = context.getSource();
        final Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        final float newHealth = FloatArgumentType.getFloat(context, "health");

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
            lastModified = (LivingEntity) entity;
            if (CommandLogic.setHealthSingle(lastModified, newHealth, updateMaxHealth)) {
                i++;
            }
        }

        // send response
        if (i == 0) {
            source.sendFailure(new StringTextComponent("There were no entities with a health attribute in your selection."));
        } else if (i == 1) {
            final String m = "New health of " + lastModified.getName().getString() + " is " + lastModified.getHealth();
            source.sendSuccess(new StringTextComponent(m), false);
        } else {
            final String m = "New health of " + i + " entities is now " + newHealth;
            source.sendSuccess(new StringTextComponent(m), false);
        }
        return i;
    }

    private static int getHealth(CommandContext<CommandSource> context) throws CommandSyntaxException {
        final CommandSource source = context.getSource();
        final Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        int lastSeenHealth = 0;

        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            float health = livingEntity.getHealth();
            lastSeenHealth = Math.round(health);
            source.sendSuccess(new StringTextComponent(livingEntity.getName().getString() + " has " + health + " health left."), false);
        }

        if (targets.size() == 1) {
            return lastSeenHealth;
        }
        return targets.size();
    }

    private static int resetHealth(CommandContext<CommandSource> context) throws CommandSyntaxException {
        final CommandSource source = context.getSource();
        final Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            ModifiableAttributeInstance attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (attribute == null) {
                continue;
            }
            attribute.removeModifier(UUID);
            source.sendSuccess(new StringTextComponent("Resetted the health of " + livingEntity.getName().getString()), false);
            i++;
        }
        return i;
    }

    // #################################################
    // HELPER METHODS
    // #################################################

    private static int addHealth(CommandContext<CommandSource> context) throws CommandSyntaxException {
        return addHealth(context, true);
    }

    private static int setHealth(CommandContext<CommandSource> context) throws CommandSyntaxException {
        return setHealth(context, true);
    }

}
