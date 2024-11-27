package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pengwz.dynamic.sql2.plugins.conversion.FetchResultConverter;

import java.util.Map;

public class FetchJsonObjectConverter implements FetchResultConverter<JsonObject> {

    @Override
    public JsonObject convertValueTo(Map<String, Object> value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(value), JsonObject.class);
    }
}
