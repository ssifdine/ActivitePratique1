package ma.saifdine.hd.customerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CacheLoggingAspect {

    private final CacheManager cacheManager;


    public CacheLoggingAspect(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Around("@annotation(cacheable)")
    public Object logCache(ProceedingJoinPoint pjp, Cacheable cacheable) throws Throwable {

        String cacheName = cacheable.value()[0];
        Object key = pjp.getArgs().length == 0 ? SimpleKey.EMPTY : new SimpleKey(pjp.getArgs());

        Cache cache = cacheManager.getCache(cacheName);
        boolean fromCache = cache != null && cache.get(cacheName) != null;

        if (fromCache) {
            log.info("⚡ FROM REDIS | Cache: {} | Key: {}", cacheName, key);
        } else {
            log.info("🔍 FROM DATABASE | Cache: {} | Key: {}", cacheName, key);
        }

        return pjp.proceed();
    }
}
