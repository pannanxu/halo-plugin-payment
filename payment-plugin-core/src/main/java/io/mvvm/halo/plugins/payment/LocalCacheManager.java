package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.cache.CacheBody;
import io.mvvm.halo.plugins.payment.sdk.cache.CacheManager;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * LocalCacheManager.
 *
 * @author: pan
 **/
@Component
public class LocalCacheManager implements CacheManager {

    private final Map<String, Wrapper> cacheMap = new ConcurrentHashMap<>();

    public LocalCacheManager() {
        Timer timer = new Timer("LocalCacheManager_Timer", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (String key : cacheMap.keySet()) {
                        Wrapper wrapper = cacheMap.get(key);
                        if (null == wrapper || wrapper.isExpired()) {
                            cacheMap.remove(key);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }, 3000, 3000);
    }

    @Override
    public <T> void set(@NonNull String key, T data) {
        set(key, data, null, null);
    }

    @Override
    public <T> void set(@NonNull String key, T data, Integer expire) {
        set(key, data, expire, null);
    }

    @Override
    public <T> void set(@NonNull String key, T data, Integer expire, Function<String, CacheBody> fn) {
        Wrapper wrapper = new Wrapper();
        wrapper.setExpire(expire);
        wrapper.setData(data);
        wrapper.setKey(key);
        wrapper.setFn(fn);
        cacheMap.put(key, wrapper);
    }

    @Override
    @SuppressWarnings("all")
    public <T> Optional<T> get(@NonNull String key) {
        Wrapper wrapper = cacheMap.get(key);
        if (null == wrapper || wrapper.isExpired() || null == wrapper.getData()) {
            cacheMap.remove(key);
            return Optional.empty();
        }
        return Optional.of((T) wrapper.getData());
    }

    @Data
    private static class Wrapper {
        private String key;
        private Object data;
        private Class<?> dataClazz;
        private Long expire;
        private Function<String, CacheBody> fn;

        public void setExpire(Integer expire) {
            if (null != expire) {
                this.expire = (expire * 1000L) + System.currentTimeMillis();
            }
        }

        public boolean isExpired() {
            if (null == this.expire) {
                return false;
            }
            if (this.expire <= System.currentTimeMillis()) {
                if (null != fn) {
                    CacheBody body = fn.apply(this.key);
                    if (null != body) {
                        setData(body.getData());
                        setExpire(body.getExpire());
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
