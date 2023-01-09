package io.mvvm.halo.plugins.payment.sdk.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.util.Collection;
import java.util.Map;

/**
 * @author: Pan
 **/
public final class GsonUtils {

    private final Gson gson;

    private GsonUtils(Gson gson) {
        this.gson = gson;
    }

    public static GsonUtils getInstance(Gson gson) {
        return new GsonUtils(gson);
    }

    public static JsonObject parse(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static JsonArray parseArray(String json) {
        return JsonParser.parseString(json).getAsJsonArray();
    }

    public static JsonObject parse(Reader json) {
        return JsonParser.parseReader(json).getAsJsonObject();
    }

    public static JsonObject parse(JsonReader json) {
        return JsonParser.parseReader(json).getAsJsonObject();
    }

    public <T> String toJsonString(T t) {
        if (null == t) {
            return null;
        }
        return gson.toJson(t);
    }

    public <T> T toObject(String json, Class<T> clazz) {
        if (null == json || "".equals(json)) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public <K, V> Map<K, V> toMap(String json, Class<K> key, Class<V> value) {
        if (null == json || "".equals(json)) {
            return null;
        }
        return gson.fromJson(json, new TypeToken<Map<K, V>>() {
        }.getType());
    }

    public <T> Collection<T> toList(String json, Class<T> clazz) {
        return gson.fromJson(json, new TypeToken<Collection<T>>() {
        }.getType());
    }

    public Gson getGson() {
        return gson;
    }
}
