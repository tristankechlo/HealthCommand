package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.net.URISyntaxException;

public enum ProjectLinks {

    GITHUB("https://github.com/tristankechlo/HealthCommand", "Check out the source code on GitHub: "),
    ISSUE(GITHUB.url + "/issues", "If you found an issue, submit it here: "),
    WIKI(GITHUB.url + "/wiki", "Explanation for the command and config: "),
    DISCORD("https://discord.gg/bhUaWhq", "Join the Discord here: "),
    CURSEFORGE("https://curseforge.com/minecraft/mc-mods/health-command", "Check out the CurseForge page here: "),
    MODRINTH("https://modrinth.com/mod/health-command", "Check out the Modrinth page here: ");

    private final String url;
    private final String message;

    ProjectLinks(String url, String message) {
        this.url = url;
        this.message = message;
    }

    public int execute(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        try {
            IFormattableTextComponent link = ModCommand.clickableLink(this.url);
            IFormattableTextComponent message = new StringTextComponent(this.message).withStyle(TextFormatting.WHITE).append(link);
            ModCommand.sendMessage(source, message, false);
        } catch (URISyntaxException e) {
            ConfigManager.LOGGER.error(e);
            ModCommand.sendMessage(source, new StringTextComponent("An error occurred! Please check the server console!").withStyle(TextFormatting.RED), true);
        }
        return 1;
    }

}
