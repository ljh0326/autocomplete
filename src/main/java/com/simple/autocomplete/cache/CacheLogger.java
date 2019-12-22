package com.simple.autocomplete.cache;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AIS 정훈
 * @contact jhlee@saltlux.com
 * CacheEvent를 감지해서 LOGGER를 출력하는 클래스
 */
public class CacheLogger implements CacheEventListener<Object, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheLogger.class);

    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        LOGGER.info("Key: { } | EventType: {} | Old value: {} | New value: {}",
                cacheEvent.getKey(), cacheEvent.getType(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
