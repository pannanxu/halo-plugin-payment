package net.nanxu.payment.cache;

/**
 * CacheManager.
 *
 * @author: P
 **/
public interface CacheManager {
    
    void put(String key, Object value);
    
    <T> T get(String key, Class<T> clazz);
    
    void remove(String key);

}
