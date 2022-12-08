package com.tristankechlo.healthcommand.config.values;

import com.google.gson.JsonObject;

public interface IConfigValue<T> {

    String getIdentifier();

    void setToDefault();

    T get();

    void serialize(JsonObject jsonObject);

    void deserialize(JsonObject jsonObject);

}
