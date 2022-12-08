package com.tristankechlo.healthcommand.commands;

import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.config.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public final class ResponseHelper {

    public static void sendMessageConfigShow(ServerCommandSource source) {
        MutableText clickableFile = clickableConfig();
        MutableText message = new LiteralText("Config-file can be found here: ").append(clickableFile);
        sendMessage(source, message.formatted(Formatting.WHITE), false);
    }

    public static void sendMessageConfigReload(ServerCommandSource source) {
        MutableText message = new LiteralText("Config was successfully reloaded.");
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static void sendMessageConfigReset(ServerCommandSource source) {
        MutableText message = new LiteralText("Config was successfully set to default.");
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static MutableText start() {
        return new LiteralText("[" + HealthCommandMain.MOD_NAME + "] ").formatted(Formatting.GOLD);
    }

    public static void sendMessage(ServerCommandSource source, MutableText message, boolean broadcastToOps) {
        MutableText start = start().append(message);
        source.sendFeedback(start, broadcastToOps);
    }

    public static MutableText clickableConfig() {
        String fileName = ConfigManager.FILE_NAME;
        String filePath = ConfigManager.getConfigPath();
        MutableText mutableComponent = new LiteralText(fileName);
        mutableComponent.formatted(Formatting.GREEN, Formatting.UNDERLINE);
        mutableComponent.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

    public static MutableText clickableLink(String url, String displayText) {
        MutableText mutableComponent = new LiteralText(displayText);
        mutableComponent.formatted(Formatting.GREEN, Formatting.UNDERLINE);
        mutableComponent.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableText clickableLink(String url) {
        return clickableLink(url, url);
    }

}
