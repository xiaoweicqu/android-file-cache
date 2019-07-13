package com.example.cachemanager.cache;

import android.support.annotation.Nullable;

/**
 * Interface for cache.
 *
 * @param <T> type of the cache value
 */
public interface Cache <T> {
    /**
     * Gets the value associated with the key in the cache, or null if there's no cached value
     *
     * @param key unique key for the value
     * @return value if exists, otherwise, return null
     */
    @Nullable
    T get(String key);

    /**
     * Puts the value to the cache.
     *
     * @param key unique key for the value
     * @param value value to cache
     * @return true if success
     */
    boolean put(String key, T value);

    /**
     * Clears all data in cache.
     *
     * @return true if success
     */
    boolean clearAll();
}
