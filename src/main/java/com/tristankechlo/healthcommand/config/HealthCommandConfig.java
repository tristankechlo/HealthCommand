package com.tristankechlo.healthcommand.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HealthCommandConfig {

    /* the permission level needed to execute this command */
    private final int permissionLevel;
    /* whether or not to fire the LivingHealEvent when healing entities */
    private final boolean fireLivingHealEvent;

    public HealthCommandConfig(int permissionLevel, boolean fireLivingHealEvent) {
        this.permissionLevel = permissionLevel;
        this.fireLivingHealEvent = fireLivingHealEvent;
    }

    public static final Codec<HealthCommandConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.intRange(0, 4).fieldOf("requiredPermissionLevel").forGetter(HealthCommandConfig::permissionLevel),
                    Codec.BOOL.fieldOf("fireLivingHealEvent").forGetter(HealthCommandConfig::fireLivingHealEvent)
            ).apply(instance, HealthCommandConfig::new)
    );

    private static HealthCommandConfig INSTANCE = new HealthCommandConfig(2, true);

    public static HealthCommandConfig get() {
        return INSTANCE;
    }

    public static void setToDefault() {
        INSTANCE = new HealthCommandConfig(2, true);
    }

    public static JsonElement serialize() {
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE);
        result.error().ifPresent((partial) -> ConfigManager.LOGGER.error(partial.message()));
        return result.result().orElseThrow(() -> new JsonParseException("Failed to serialize config"));
    }

    public static void deserialize(JsonElement json) {
        DataResult<HealthCommandConfig> result = CODEC.parse(JsonOps.INSTANCE, json);
        result.error().ifPresent((partial) -> ConfigManager.LOGGER.error(partial.message()));
        INSTANCE = result.result().orElseThrow(() -> new JsonParseException("Failed to deserialize config"));
    }

    public int permissionLevel() {
        return this.permissionLevel;
    }

    public boolean fireLivingHealEvent() {
        return this.fireLivingHealEvent;
    }

}
