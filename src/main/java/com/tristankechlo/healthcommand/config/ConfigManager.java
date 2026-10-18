package com.tristankechlo.healthcommand.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public final class ConfigManager {

    public static final Logger LOGGER = LogManager.getLogger("HealthCommand");
    private static File CONFIG_DIR;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static final String FILE_NAME = "healthcommand.json";
    private static File CONFIG_FILE;

    public static void loadAndVerifyConfig(Path configDir) {
        ConfigManager.CONFIG_DIR = configDir.toFile();
        ConfigManager.CONFIG_FILE = new File(CONFIG_DIR, FILE_NAME);

        ConfigManager.createConfigFolder();

        if (!CONFIG_FILE.exists()) {
            HealthCommandConfig.setToDefault();
            ConfigManager.writeConfigToFile();
            LOGGER.warn("No config '{}' was found, created a new one.", FILE_NAME);
            return;
        }

        try {
            ConfigManager.loadConfigFromFile();
            LOGGER.info("Config '{}' was successfully loaded.", FILE_NAME);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.error("Error loading config '{}', config hasn't been loaded. Using default values.", FILE_NAME);
            HealthCommandConfig.setToDefault();
        }
    }

    private static void loadConfigFromFile() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(CONFIG_FILE));
        JsonObject json = jsonElement.getAsJsonObject();
        HealthCommandConfig.deserialize(json);
    }

    private static void writeConfigToFile() {
        try {
            JsonElement jsonObject = HealthCommandConfig.serialize();
            JsonWriter writer = new JsonWriter(new FileWriter(CONFIG_FILE));
            writer.setIndent("\t");
            GSON.toJson(jsonObject, writer);
            writer.close();
        } catch (Exception e) {
            LOGGER.error("There was an error writing the config to file: '{}'", FILE_NAME);
            LOGGER.error(e.getMessage());
        }
    }

    public static void resetConfig() {
        HealthCommandConfig.setToDefault();
        ConfigManager.writeConfigToFile();
        LOGGER.info("Config '{}' was set to default.", FILE_NAME);
    }

    public static void reloadConfig() {
        if (CONFIG_FILE.exists()) {
            try {
                ConfigManager.loadConfigFromFile();
                LOGGER.info("The config '{}' was successfully loaded.", FILE_NAME);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Error loading config '{}', config hasn't been loaded. Using the default values.", FILE_NAME);
                HealthCommandConfig.setToDefault();
            }
        } else {
            HealthCommandConfig.setToDefault();
            ConfigManager.writeConfigToFile();
            LOGGER.warn("No config '{}' found, created a new one.", FILE_NAME);
        }
    }

    public static String getConfigPath() {
        return CONFIG_FILE.getAbsolutePath();
    }

    private static void createConfigFolder() {
        if (!CONFIG_DIR.exists()) {
            if (!CONFIG_DIR.mkdirs()) {
                throw new RuntimeException("Could not create config folder: " + CONFIG_DIR.getAbsolutePath());
            }
        }
    }

}

