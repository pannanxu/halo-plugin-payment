package net.nanxu.payment.utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * JsonNodeUtils.
 *
 * @author: P
 **/
public class JsonNodeUtils {
    
    public static String getString(JsonNode node, String field) {
        return getString(node, field, null);
    }
    
    public static String getString(JsonNode node, String field, String defaultValue) {
        return node.has(field) ? node.get(field).asText() : defaultValue;
    }
}
