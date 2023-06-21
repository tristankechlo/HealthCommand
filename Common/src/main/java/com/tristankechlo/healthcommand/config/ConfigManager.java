package com.tristankechlo.healthcommand.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.tristankechlo.healthcommand.HealthCommandMain;
import com.tristankechlo.healthcommand.platform.IPlatformHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class ConfigManager {

    private static final File CONFIG_DIR = IPlatformHelper.INSTANCE.getConfigDirectory().toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static final String FILE_NAME = HealthCommandMain.MOD_ID + ".json";

    public static void loadAndVerifyConfig() {
        ConfigManager.createConfigFolder();
        HealthCommandConfig.setToDefault();
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        if (configFile.exists()) {
            ConfigManager.loadConfigFromFile(configFile);
            ConfigManager.writeConfigToFile(configFile);
            HealthCommandMain.LOGGER.info("Saved the checked/corrected config: '{}'", FILE_NAME);
        } else {
            ConfigManager.writeConfigToFile(configFile);
            HealthCommandMain.LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
        }
    }

    private static void writeConfigToFile(File file) {
        JsonObject jsonObject = HealthCommandConfig.serialize(new JsonObject());
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("\t");
            GSON.toJson(jsonObject, writer);
            writer.close();
        } catch (Exception e) {
            HealthCommandMain.LOGGER.error("There was an error writing the config to file: '{}'", FILE_NAME);
            e.printStackTrace();
        }
    }

    private static void loadConfigFromFile(File file) {
        JsonObject json = null;
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
            json = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            HealthCommandMain.LOGGER.error("There was an error loading the config file: '{}'", FILE_NAME);
            e.printStackTrace();
        }
        if (json != null) {
            HealthCommandConfig.deserialize(json);
            HealthCommandMain.LOGGER.info("Config '{}' was successfully loaded.", FILE_NAME);
        } else {
            HealthCommandMain.LOGGER.error("Error loading config '{}', config hasn't been loaded.", FILE_NAME);
        }
    }

    public static void resetConfig() {
        HealthCommandConfig.setToDefault();
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        ConfigManager.writeConfigToFile(configFile);
        HealthCommandMain.LOGGER.info("Config '{}' was set to default.", FILE_NAME);
    }

    public static void reloadConfig() {
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        if (configFile.exists()) {
            ConfigManager.loadConfigFromFile(configFile);
            ConfigManager.writeConfigToFile(configFile);
            HealthCommandMain.LOGGER.info("Saved the checked/corrected config: " + FILE_NAME);
        } else {
            ConfigManager.writeConfigToFile(configFile);
            HealthCommandMain.LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
        }
    }

    public static String getConfigPath() {
        File configFile = new File(CONFIG_DIR, FILE_NAME);
        return configFile.getAbsolutePath();
    }

    private static void createConfigFolder() {
        if (!CONFIG_DIR.exists()) {
            if (!CONFIG_DIR.mkdirs()) {
                throw new RuntimeException("Could not create config folder: " + CONFIG_DIR.getAbsolutePath());
            }
        }
    }

}

