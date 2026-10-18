package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.net.URISyntaxException;

import static net.minecraft.command.Commands.literal;

public class ModCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> command = literal("healthcommand")
                .then(literal("config").requires((source) -> source.hasPermission(3))
                        .then(literal("reload").executes(ModCommand::configReload))
                        .then(literal("reset").executes(ModCommand::configReset)))
                .then(literal("github").executes(ProjectLinks.GITHUB::execute))
                .then(literal("issue").executes(ProjectLinks.ISSUE::execute))
                .then(literal("wiki").executes(ProjectLinks.WIKI::execute))
                .then(literal("discord").executes(ProjectLinks.DISCORD::execute))
                .then(literal("curseforge").executes(ProjectLinks.CURSEFORGE::execute))
                .then(literal("modrinth").executes(ProjectLinks.MODRINTH::execute));
        dispatcher.register(command);
        ConfigManager.LOGGER.info("Command '/healthcommand' registered");
    }

    private static int configReload(CommandContext<CommandSource> context) {
        ConfigManager.reloadConfig();
        IFormattableTextComponent message = new StringTextComponent("Config was successfully reloaded.");
        sendMessage(context.getSource(), message.withStyle(TextFormatting.WHITE), true);
        return 1;
    }

    private static int configReset(CommandContext<CommandSource> context) {
        ConfigManager.resetConfig();
        IFormattableTextComponent message = new StringTextComponent("Config was successfully set to default.");
        sendMessage(context.getSource(), message.withStyle(TextFormatting.WHITE), true);
        return 1;
    }

    // #################################################
    // HELPER METHODS
    // #################################################

    private static IFormattableTextComponent start() {
        return new StringTextComponent("[HealthCommand] ").withStyle(TextFormatting.GOLD);
    }

    public static void sendMessage(CommandSource source, IFormattableTextComponent message, boolean broadcastToOps) {
        IFormattableTextComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    private static IFormattableTextComponent clickableLink(String url, String displayText) {
        IFormattableTextComponent mutableComponent = new StringTextComponent(displayText);
        mutableComponent.withStyle(TextFormatting.GREEN, TextFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static IFormattableTextComponent clickableLink(String url) throws URISyntaxException {
        return clickableLink(url, url);
    }
}
