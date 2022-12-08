package com.tristankechlo.healthcommand.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.healthcommand.HealthCommandMain;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public final class IntegerValue implements IConfigValue<Integer> {

    private final String identifier;
    private final Integer defaultValue;
    private Integer value;
    private final Integer minValue;
    private final Integer maxValue;

    public IntegerValue(String identifier, Integer defaultValue, Integer minValue, Integer maxValue) {
        this.identifier = identifier;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setToDefault() {
        this.value = defaultValue;
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(getIdentifier(), get());
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            if (GsonHelper.isNumberValue(jsonObject, getIdentifier())) {
                int integer = GsonHelper.getAsInt(jsonObject, getIdentifier(), defaultValue);
                value = Mth.clamp(integer, minValue, maxValue);
                return;
            } else {
                HealthCommandMain.LOGGER.warn("Config value '{}' was not found or is not a valid integer, using default value '{}' instead", getIdentifier(), defaultValue);
            }
        } catch (Exception e) {
            HealthCommandMain.LOGGER.warn("Error while loading the config value '{}', using default value '{}' instead", getIdentifier(), defaultValue);
        }
        this.setToDefault();
    }

}
