package ${package}.support.redis;

import io.elves.common.util.ResourceUtil;
import io.elves.core.properties.ElvesProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseRedisClient {
    private static final Object OBJ = new Object();
    private static RedissonClient client;

    public BaseRedisClient() {

    }

    private void init() {
        try {
            Config config = Config.fromYAML(
                    ResourceUtil.getResourceAsStream(Thread.currentThread().getContextClassLoader()
                            , String.format("redis-%s.yaml", ElvesProperties.getProfileActive())));
            client = Redisson.create(config);
        } catch (IOException e) {
            log.error("redis init failed ", e);
            throw new RuntimeException(e);
        }
    }

    protected void close() {
        client().shutdown(1, 3, TimeUnit.SECONDS);
    }

    protected RedissonClient client() {
        if (client == null) {
            synchronized (OBJ) {
                if (client == null) {
                    init();
                }
            }
        }
        return client;
    }
}
