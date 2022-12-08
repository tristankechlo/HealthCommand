package com.tristankechlo.healthcommand.config;

import com.google.gson.JsonObject;
import com.tristankechlo.healthcommand.config.values.BooleanValue;
import com.tristankechlo.healthcommand.config.values.IntegerValue;

public final class HealthCommandConfig {

    /* the permission level needed to execute this command */
    public static final IntegerValue permissionLevel = new IntegerValue("requiredPermissionLevel", 2, 0, 4);
    /* whether or not the health of the entity should increase beyond the maximum health for adding health */
    public static final BooleanValue goBeyondMaxHealthForAdding = new BooleanValue("goBeyondMaxHealthForAdding", true);
    /* whether or not the health of the entity should increase beyond the maximum health for setting health */
    public static final BooleanValue goBeyondMaxHealthForSetting = new BooleanValue("goBeyondMaxHealthForSetting", true);

    public static void setToDefault() {
        permissionLevel.setToDefault();
        goBeyondMaxHealthForAdding.setToDefault();
        goBeyondMaxHealthForSetting.setToDefault();
    }

    public static JsonObject serialize(JsonObject json) {
        permissionLevel.serialize(json);
        goBeyondMaxHealthForAdding.serialize(json);
        goBeyondMaxHealthForSetting.serialize(json);
        return json;
    }

    public static void deserialize(JsonObject json) {
        permissionLevel.deserialize(json);
        goBeyondMaxHealthForAdding.deserialize(json);
        goBeyondMaxHealthForSetting.deserialize(json);
    }

}
