package io.mvvm.halo.plugins.payment.sdk.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: Pan
 **/
public class MapUtils {

    public static String sortToString(final Map<String, String> data) {
        return sortToString(data, "&");
    }

    public static String sortToString(final Map<String, String> data, String join) {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            sb.append(k).append("=").append(data.get(k).trim()).append(join);
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getUrlParam(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder.append(urlEncoder(entry.getKey())).append('=').append(urlEncoder(entry.getValue()));
        }
        return builder.toString();
    }

    public static String urlEncoder(String src) {
        return URLEncoder.encode(src, StandardCharsets.UTF_8);
    }

}
