package com.tristankechlo.healthcommand.commands;

import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source) {
        MutableComponent clickableFile = clickableConfig();
        MutableComponent message = new TextComponent("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source) {
        MutableComponent message = new TextComponent("Config was successfully reloaded.");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source) {
        MutableComponent message = new TextComponent("Config was successfully set to default.");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static MutableComponent start() {
        return new TextComponent("[" + HealthCommandMain.MOD_NAME + "] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    public static MutableComponent clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableComponent mutableComponent = new TextComponent(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = new TextComponent(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink(String url) {
        return clickableLink(url, url);
    }

}
