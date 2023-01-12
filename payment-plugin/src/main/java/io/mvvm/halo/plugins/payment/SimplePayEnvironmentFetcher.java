package io.mvvm.halo.plugins.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.SystemSetting;
import run.halo.app.infra.utils.JsonParseException;
import run.halo.app.infra.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A fetcher that fetches the system configuration from the extension client.
 * If there are {@link ConfigMap}s named <code>system-default</code> and <code>system</code> at
 * the same time, the {@link ConfigMap} named system will be json merge patch to
 * {@link ConfigMap} named <code>system-default</code>
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
public class SimplePayEnvironmentFetcher implements PayEnvironmentFetcher {
    private final ReactiveExtensionClient extensionClient;

    public SimplePayEnvironmentFetcher(ReactiveExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }

    @Override
    public <T> Mono<T> fetchSystemConfig(String key, Class<T> type) {
        return fetch(SystemSetting.SYSTEM_CONFIG_DEFAULT, key, type);
    }

    @Override
    public Mono<PaymentSetting> fetchPaymentConfig(String key) {
        return fetch(PaymentSetting.name, key, PaymentSetting.class);
    }

    @Override
    public <T> Mono<T> fetch(String name, String key, Class<T> type) {
        return getValuesInternal(name)
                .filter(map -> map.containsKey(key))
                .map(map -> map.get(key))
                .mapNotNull(stringValue -> JsonUtils.jsonToObject(stringValue, type));
    }

    @NonNull
    private Mono<Map<String, String>> getValuesInternal(String name) {
        return getConfigMap(name)
                .filter(configMap -> configMap.getData() != null)
                .map(ConfigMap::getData)
                .defaultIfEmpty(Map.of());
    }

    /**
     * Gets config map.
     *
     * @return a new {@link ConfigMap} named <code>system</code> by json merge patch.
     */
    @Override
    public Mono<ConfigMap> getConfigMap(String name) {
        Mono<ConfigMap> mapMono =
                extensionClient.fetch(ConfigMap.class, name);
        if (mapMono == null) {
            return Mono.empty();
        }
        return mapMono.flatMap(systemDefault ->
                extensionClient.fetch(ConfigMap.class, SystemSetting.SYSTEM_CONFIG)
                        .map(system -> {
                            Map<String, String> defaultData = systemDefault.getData();
                            Map<String, String> data = system.getData();
                            Map<String, String> mergedData = mergeData(defaultData, data);
                            system.setData(mergedData);
                            return system;
                        })
                        .switchIfEmpty(Mono.just(systemDefault)));
    }

    private Map<String, String> mergeData(Map<String, String> defaultData,
                                          Map<String, String> data) {
        if (defaultData == null) {
            return data;
        }
        if (data == null) {
            return defaultData;
        }

        Map<String, String> copiedDefault = new LinkedHashMap<>(defaultData);
        // // merge the data map entries into the default map
        data.forEach((group, dataValue) -> {
            // https://www.rfc-editor.org/rfc/rfc7386
            String defaultV = copiedDefault.get(group);
            String newValue;
            if (dataValue == null) {
                if (copiedDefault.containsKey(group)) {
                    newValue = null;
                } else {
                    newValue = defaultV;
                }
            } else {
                newValue = mergeRemappingFunction(dataValue, defaultV);
            }

            if (newValue == null) {
                copiedDefault.remove(group);
            } else {
                copiedDefault.put(group, newValue);
            }
        });
        return copiedDefault;
    }

    String mergeRemappingFunction(String dataV, String defaultV) {
        JsonNode dataJsonValue = nullSafeToJsonNode(dataV);
        // original
        JsonNode defaultJsonValue = nullSafeToJsonNode(defaultV);
        try {
            // patch
            JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(dataJsonValue);
            // apply patch to original
            JsonNode patchedNode = jsonMergePatch.apply(defaultJsonValue);
            return JsonUtils.objectToJson(patchedNode);
        } catch (JsonPatchException e) {
            throw new JsonParseException(e);
        }
    }

    JsonNode nullSafeToJsonNode(String json) {
        return StringUtils.isBlank(json) ? JsonNodeFactory.instance.nullNode()
                : JsonUtils.jsonToObject(json, JsonNode.class);
    }
}
