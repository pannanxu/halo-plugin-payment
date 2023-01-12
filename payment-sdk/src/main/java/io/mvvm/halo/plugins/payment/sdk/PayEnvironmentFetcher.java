package io.mvvm.halo.plugins.payment.sdk;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;

/**
 * A fetcher that fetches the system configuration from the extension client.
 * If there are {@link ConfigMap}s named <code>system-default</code> and <code>system</code> at
 * the same time, the {@link ConfigMap} named system will be json merge patch to
 * {@link ConfigMap} named <code>system-default</code>
 *
 * @author guqing
 * @since 2.0.0
 */
public interface PayEnvironmentFetcher {
    <T> Mono<T> fetchSystemConfig(String key, Class<T> type);

    Mono<PaymentSetting> fetchPaymentConfig(String key);

    <T> Mono<T> fetch(String name, String key, Class<T> type);

    /**
     * Gets config map.
     *
     * @return a new {@link ConfigMap} named <code>system</code> by json merge patch.
     */
    Mono<ConfigMap> getConfigMap(String name);

}
