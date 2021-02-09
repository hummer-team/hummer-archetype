package ${package}.support.redis;

import java.util.concurrent.TimeUnit;

public interface SimpleRedisOp {
    <D> void set(String key, D data, long ttl, TimeUnit timeUnit);
    <D> D get(String key);
}
