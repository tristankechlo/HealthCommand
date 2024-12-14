package com.tristankechlo.healthcommand.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public enum ProjectLinks {

    GITHUB("https://github.com/tristankechlo/HealthCommand", "Check out the source code on GitHub: "),
    ISSUE(GITHUB.url + "/issues", "If you found an issue, submit it here: "),
    DISCORD("https://discord.gg/bhUaWhq", "Join the Discord here: "),
    CURSEFORGE("https://curseforge.com/minecraft/mc-mods/health-command", "Check out the CurseForge page here: "),
    MODRINTH("https://modrinth.com/mod/health-command", "Check out the Modrinth page here: ");

    private final String url;
    private final String message;

    ProjectLinks(String url, String message) {
        this.url = url;
        this.message = message;
    }

    public int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ModCommand.clickableLink(this.url);
        Component message = Component.literal(this.message).withStyle(ChatFormatting.WHITE).append(link);
        ModCommand.sendMessage(source, message, false);
        return 1;
    }

}
