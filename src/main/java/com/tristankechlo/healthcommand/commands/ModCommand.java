package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.command.Commands.literal;

public class ModCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> command = literal(HealthCommandMain.MOD_ID)
                .then(literal("config").requires((source) -> source.hasPermission(3))
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

    private static int configReload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ConfigManager.reloadConfig();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int configReset(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ConfigManager.resetConfig();
        ResponseHelper.sendMessageConfigReset(source);
        return 1;
    }

    private static int github(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_URL);
        IFormattableTextComponent message = new StringTextComponent("Check out the source code on GitHub: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_ISSUE_URL);
        IFormattableTextComponent message = new StringTextComponent("If you found an issue, submit it here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink(HealthCommandMain.DISCORD_URL);
        IFormattableTextComponent message = new StringTextComponent("Join the Discord here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink(HealthCommandMain.CURSEFORGE_URL);
        IFormattableTextComponent message = new StringTextComponent("Check out the CurseForge page here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink(HealthCommandMain.MODRINTH_URL);
        IFormattableTextComponent message = new StringTextComponent("Check out the Modrinth page here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }
}
