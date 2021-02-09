package ${package}.support.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.elves.core.properties.ElvesProperties;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * wrapper caffeine cache.
 *
 * @author lee
 */
public class CacheWrapper {
    private static final SimpleCache<?, ?> LOCAL_CACHE = new LocalCache<>();

    public static <K, D> SimpleCache<K, D> localCache() {
        return (LocalCache<K, D>) LOCAL_CACHE;
    }

    public static class LocalCache<K, D> implements SimpleCache<K,D> {
        private static final Object OBJ = new Object();
        private volatile boolean isInit = false;
        private LoadingCache<K, D> cache;

        private LocalCache() {
            init();
        }

        private void init() {
            if (!isInit) {
                synchronized (OBJ) {
                    if (!isInit) {
                        cache = Caffeine.newBuilder()
                                .maximumSize(ElvesProperties.valueOfInteger("caffeine.cache.maximumSize"
                                        , "20000"))
                                .expireAfterAccess(ElvesProperties.valueOfInteger(
                                        "caffeine.cache.expireAfterAccessSecond"
                                        , "120")
                                        , TimeUnit.SECONDS)
                                //.refreshAfterWrite(1, TimeUnit.MINUTES)
                                .build(new CacheLoader<K, D>() {
                                    @Nullable
                                    @Override
                                    public D load(@NonNull K key) throws Exception {
                                        //load data
                                        return null;
                                    }
                                });
                        isInit = true;
                    }
                }
            }
        }

        @Override
        public void put(K key, D data) {
            cache.put(key, data);
        }

        @Override
        public D get(K key) {
            return cache.get(key);
        }

        @Override
        public void removeOf(K key) {
            cache.invalidate(key);
        }

        @Override
        public void removeOfKeys(Iterable<K> key) {
            cache.invalidateAll(key);
        }

        @Override
        public void cleanAll() {
            cache.invalidateAll();
        }
    }
}
