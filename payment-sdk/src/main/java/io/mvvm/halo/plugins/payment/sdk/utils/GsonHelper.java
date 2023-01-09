package io.mvvm.halo.plugins.payment.sdk.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * GsonHelper.
 *
 * @author: pan
 **/
public class GsonHelper {
    private final JsonObject json;

    public GsonHelper(JsonObject json) {
        this.json = json;
    }

    public static GsonHelper of(String json) {
        return new GsonHelper(GsonUtils.parse(json));
    }

    public static GsonHelper of(JsonObject json) {
        return new GsonHelper(json);
    }

    public Optional<JsonObject> getJson() {
        return Optional.ofNullable(this.json);
    }

    public String getAsString(String key, String defVal) {
        return getJson().map(json -> json.get(key)).map(JsonElement::getAsString).orElse(defVal);
    }

    public String getAsString(JsonObject json, String key, String defVal) {
        return Optional.ofNullable(json.get(key)).map(JsonElement::getAsString).orElse(defVal);
    }

    public String getAsString(String key) {
        return getAsString(key, null);
    }

    public Integer getAsInteger(String key) {
        JsonElement element = json.get(key);
        if (element.isJsonNull()) {
            return null;
        }
        return element.getAsInt();
    }

    public Date getAsDate(String key, String format) {
        String val = getAsString(key);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(val);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAsJsonArrayString(String key) {
        return getJson().map(json -> json.get(key))
                .map(JsonElement::getAsJsonArray)
                .flatMap((Function<JsonArray, Optional<List<String>>>) array ->
                        Optional.of(stringElementArrayToList(array)))
                .orElse(Lists.newArrayList());
    }

    public String getAsStringArgs(String... args) {
        return getJson().map(json -> {
            JsonObject object = json;
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (object.has(arg)) {
                    if (i == args.length - 1) {
                        return getAsString(object, arg, null);
                    }
                    object = object.getAsJsonObject(arg);
                }
            }
            return null;
        }).orElse(null);
    }

    public GsonHelper getAsWrapperArgs(String... args) {
        return getJson().map(json -> {
                    JsonObject object = json;
                    for (String arg : args) {
                        if (object.has(arg)) {
                            object = object.getAsJsonObject(arg);
                        }
                    }
                    return object;
                })
                .map(GsonHelper::of)
                .orElse(new GsonHelper(null));
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

    private List<String> stringElementArrayToList(JsonArray array) {
        List<String> list = Lists.newArrayList();
        for (JsonElement el : array) {
            list.add(el.getAsString());
        }
        return list;
    }
}
