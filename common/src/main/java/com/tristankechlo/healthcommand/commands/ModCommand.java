package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.commands.Commands.literal;

public class ModCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("healthcommand")
                .then(literal("config").requires((source) -> source.hasPermission(3))
                        .then(literal("reload").executes(ModCommand::configReload))
                        .then(literal("show").executes(ModCommand::configShow))
                        .then(literal("reset").executes(ModCommand::configReset)))
                .then(literal("github").executes(ProjectLinks.GITHUB::execute))
                .then(literal("issue").executes(ProjectLinks.ISSUE::execute))
                .then(literal("discord").executes(ProjectLinks.DISCORD::execute))
                .then(literal("curseforge").executes(ProjectLinks.CURSEFORGE::execute))
                .then(literal("modrinth").executes(ProjectLinks.MODRINTH::execute));
        dispatcher.register(command);
        ConfigManager.LOGGER.info("Command '/healthcommand' registered");
    }

    private static int configReload(CommandContext<CommandSourceStack> context) {
        ConfigManager.reloadConfig();
        MutableComponent message = Component.literal("Config was successfully reloaded.");
        sendMessage(context.getSource(), message.withStyle(ChatFormatting.WHITE), true);
        return 1;
    }

    private static int configShow(CommandContext<CommandSourceStack> context) {
        MutableComponent clickableFile = clickableConfig();
        MutableComponent message = Component.literal("Config-file can be found here: ").append(clickableFile);
        sendMessage(context.getSource(), message.withStyle(ChatFormatting.WHITE), false);
        return 1;
    }

    private static int configReset(CommandContext<CommandSourceStack> context) {
        ConfigManager.resetConfig();
        MutableComponent message = Component.literal("Config was successfully set to default.");
        sendMessage(context.getSource(), message.withStyle(ChatFormatting.WHITE), true);
        return 1;
    }

    // #################################################
    // HELPER METHODS
    // #################################################

    private static MutableComponent start() {
        return Component.literal("[HealthCommand] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(() -> start, broadcastToOps);
    }

    private static MutableComponent clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableComponent mutableComponent = Component.literal(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    private static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = Component.literal(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url) {
        return clickableLink(url, url);
    }
}
