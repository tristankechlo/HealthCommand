package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;


public class ModCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(HealthCommandMain.MOD_ID)
                .then(literal("config").requires((source) -> source.hasPermissionLevel(3))
                        .then(literal("reload").executes(ModCommand::configReload))
                        .then(literal("show").executes(ModCommand::configShow))
                        .then(literal("reset").executes(ModCommand::configReset)))
                .then(literal("github").executes(ModCommand::github))
                .then(literal("issue").executes(ModCommand::issue))
                .then(literal("discord").executes(ModCommand::discord))
                .then(literal("curseforge").executes(ModCommand::curseforge))
                .then(literal("modrinth").executes(ModCommand::modrinth));
        dispatcher.register(command);
        HealthCommandMain.LOGGER.info("Command '/{}' registered", HealthCommandMain.MOD_ID);
    }

    private static int configReload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.reloadConfig();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int configReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.resetConfig();
        ResponseHelper.sendMessageConfigReset(source);
        return 1;
    }

    private static int github(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_URL);
        MutableText message = new LiteralText("Check out the source code on GitHub: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_ISSUE_URL);
        MutableText message = new LiteralText("If you found an issue, submit it here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(HealthCommandMain.DISCORD_URL);
        MutableText message = new LiteralText("Join the Discord here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(HealthCommandMain.CURSEFORGE_URL);
        MutableText message = new LiteralText("Check out the CurseForge page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink(HealthCommandMain.MODRINTH_URL);
        MutableText message = new LiteralText("Check out the Modrinth page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }
}
