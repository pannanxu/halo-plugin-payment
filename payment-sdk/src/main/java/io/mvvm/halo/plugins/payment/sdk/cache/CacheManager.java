package io.mvvm.halo.plugins.payment.sdk.cache;

import java.util.Optional;
import java.util.function.Function;

/**
 * CacheManager.
 *
 * @author: pan
 **/
public interface CacheManager {

    <T> void set(String key, T data);
    
    <T> void set(String key, T data, Integer expire);

    <T> void set(String key, T data, Integer expire, Function<String, CacheBody> fn);

    <T> Optional<T> get(String key);

}
