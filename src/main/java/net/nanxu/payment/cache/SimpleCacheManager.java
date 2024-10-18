package net.nanxu.payment.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * SimpleCacheManager.
 *
 * @author: P
 **/
@Component
public class SimpleCacheManager implements CacheManager {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = cache.get(key);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

}
