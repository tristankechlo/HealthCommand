package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.HealthCommandMain;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.command.Commands.literal;

public class ModCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> command = literal(HealthCommandMain.MOD_ID)
                .then(literal("config").requires((source) -> source.hasPermissionLevel(3))
                        .then(literal("show").executes(ModCommand::configShow)))
                .then(literal("github").executes(ModCommand::github))
                .then(literal("issue").executes(ModCommand::issue))
                .then(literal("discord").executes(ModCommand::discord))
                .then(literal("curseforge").executes(ModCommand::curseforge))
                .then(literal("modrinth").executes(ModCommand::modrinth));
        dispatcher.register(command);
        HealthCommandMain.LOGGER.info("Command '/{}' registered", HealthCommandMain.MOD_ID);
    }

    private static int configShow(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ResponseHelper.sendMessageConfigShow(source);
        return 1;
    }

    private static int github(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        TextComponent link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_URL);
        TextComponent message = new StringTextComponent("Check out the source code on GitHub: ");
        message.getStyle().setColor(TextFormatting.WHITE);
        message.getSiblings().add(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        TextComponent link = ResponseHelper.clickableLink(HealthCommandMain.GITHUB_ISSUE_URL);
        TextComponent message = new StringTextComponent("If you found an issue, submit it here: ");
        message.getStyle().setColor(TextFormatting.WHITE);
        message.getSiblings().add(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        TextComponent link = ResponseHelper.clickableLink(HealthCommandMain.DISCORD_URL);
        TextComponent message = new StringTextComponent("Join the Discord here: ");
        message.getStyle().setColor(TextFormatting.WHITE);
        message.getSiblings().add(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        TextComponent link = ResponseHelper.clickableLink(HealthCommandMain.CURSEFORGE_URL);
        TextComponent message = new StringTextComponent("Check out the CurseForge page here: ");
        message.getStyle().setColor(TextFormatting.WHITE);
        message.getSiblings().add(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        TextComponent link = ResponseHelper.clickableLink(HealthCommandMain.MODRINTH_URL);
        TextComponent message = new StringTextComponent("Check out the Modrinth page here: ");
        message.getStyle().setColor(TextFormatting.WHITE);
        message.getSiblings().add(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
