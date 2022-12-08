package com.tristankechlo.healthcommand.config.values;

import com.google.gson.JsonObject;
import com.tristankechlo.healthcommand.HealthCommandMain;
import net.minecraft.util.GsonHelper;

public final class BooleanValue implements IConfigValue<Boolean> {

    private final String identifier;
    private final Boolean defaultValue;
    private Boolean value;

    public BooleanValue(String identifier, Boolean defaultValue) {
        this.identifier = identifier;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
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
    public Boolean get() {
        return value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(getIdentifier(), get());
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            if (GsonHelper.isBooleanValue(jsonObject, getIdentifier())) {
                value = GsonHelper.getAsBoolean(jsonObject, getIdentifier(), defaultValue);
                return;
            } else {
                HealthCommandMain.LOGGER.warn("Config value '{}' was not found or is not a valid boolean, using default value '{}' instead", getIdentifier(), defaultValue);
            }
        } catch (Exception e) {
            HealthCommandMain.LOGGER.warn("Error while loading the config value '{}', using default value '{}' instead", getIdentifier(), defaultValue);
        }
        this.setToDefault();
    }

}
