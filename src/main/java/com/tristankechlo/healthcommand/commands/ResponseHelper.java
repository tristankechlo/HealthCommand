package com.tristankechlo.healthcommand.commands;

import com.tristankechlo.healthcommand.HealthCommandMain;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSource source) {
        TextComponent clickableFile = clickableConfig();
        TextComponent message = new StringTextComponent("Config-file can be found here: ");
        message.getSiblings().add(clickableFile);
        message.getStyle().setColor(TextFormatting.WHITE);
        sendMessage(source, message, false);
    }

    public static TextComponent start() {
        TextComponent message = new StringTextComponent("[" + HealthCommandMain.MOD_NAME + "] ");
        message.getStyle().setColor(TextFormatting.GOLD);
        return message;
    }

    public static void sendMessage(CommandSource source, TextComponent message, boolean broadcastToOps) {
        TextComponent start = start();
        start.getSiblings().add(message);
        source.sendFeedback(start, broadcastToOps);
    }

    public static TextComponent clickableConfig() {
        String fileName = "healthcommand-common.toml";
        File f = new File(FMLPaths.CONFIGDIR.get().toFile(), fileName);
        String filePath = f.getAbsolutePath();
        TextComponent mutableComponent = new StringTextComponent(fileName);
        mutableComponent.getStyle().setColor(TextFormatting.GREEN);
        mutableComponent.getStyle().setUnderlined(true);
        mutableComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath));
        return mutableComponent;
    }

    public static TextComponent clickableLink(String url, String displayText) {
        TextComponent mutableComponent = new StringTextComponent(displayText);
        mutableComponent.getStyle().setColor(TextFormatting.GREEN);
        mutableComponent.getStyle().setUnderlined(true);
        mutableComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return mutableComponent;
    }

    public static TextComponent clickableLink(String url) {
        return clickableLink(url, url);
    }

}
