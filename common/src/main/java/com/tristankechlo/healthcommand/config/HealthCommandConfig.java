package com.tristankechlo.healthcommand.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HealthCommandConfig(
        /* the permission level needed to execute this command */
        int permissionLevel,
        /* whether or not the health of the entity should increase beyond the maximum health for adding health */
        boolean goBeyondMaxHealthForAdding,
        /* whether or not the health of the entity should increase beyond the maximum health for setting health */
        boolean goBeyondMaxHealthForSetting
) {

    public static final Codec<HealthCommandConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.intRange(0, 4).fieldOf("requiredPermissionLevel").forGetter(HealthCommandConfig::permissionLevel),
                    Codec.BOOL.fieldOf("goBeyondMaxHealthForAdding").forGetter(HealthCommandConfig::goBeyondMaxHealthForAdding),
                    Codec.BOOL.fieldOf("goBeyondMaxHealthForSetting").forGetter(HealthCommandConfig::goBeyondMaxHealthForSetting)
            ).apply(instance, HealthCommandConfig::new)
    );
    private static HealthCommandConfig INSTANCE = new HealthCommandConfig(2, true, true);

    public static HealthCommandConfig get() {
        return INSTANCE;
    }

    public static void setToDefault() {
        INSTANCE = new HealthCommandConfig(2, true, true);
    }

    public static JsonElement serialize() {
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE);
        result.error().ifPresent((partial) -> ConfigManager.LOGGER.error(partial.message()));
        return result.result().orElseThrow();
    }

    public static void deserialize(JsonElement json) {
        DataResult<HealthCommandConfig> result = CODEC.parse(JsonOps.INSTANCE, json);
        result.error().ifPresent((partial) -> ConfigManager.LOGGER.error(partial.message()));
        INSTANCE = result.result().orElseThrow();
    }

}
