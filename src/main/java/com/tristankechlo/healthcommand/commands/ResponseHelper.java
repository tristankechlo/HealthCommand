package com.tristankechlo.healthcommand.commands;

import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSource source) {
        IFormattableTextComponent clickableFile = clickableConfig();
        IFormattableTextComponent message = new StringTextComponent("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.withStyle(TextFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSource source) {
        IFormattableTextComponent message = new StringTextComponent("Config was successfully reloaded.");
        sendMessage(source, message.withStyle(TextFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSource source) {
        IFormattableTextComponent message = new StringTextComponent("Config was successfully set to default.");
        sendMessage(source, message.withStyle(TextFormatting.WHITE), true);
    }

    public static IFormattableTextComponent start() {
        return new StringTextComponent("[" + HealthCommandMain.MOD_NAME + "] ").withStyle(TextFormatting.GOLD);
    }

    public static void sendMessage(CommandSource source, ITextComponent message, boolean broadcastToOps) {
        ITextComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    public static IFormattableTextComponent clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        IFormattableTextComponent mutableComponent = new StringTextComponent(fileName);
        mutableComponent.withStyle(TextFormatting.GREEN, TextFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    public static IFormattableTextComponent clickableLink(String url, String displayText) {
        IFormattableTextComponent mutableComponent = new StringTextComponent(displayText);
        mutableComponent.withStyle(TextFormatting.GREEN, TextFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static IFormattableTextComponent clickableLink(String url) {
        return clickableLink(url, url);
    }

}
