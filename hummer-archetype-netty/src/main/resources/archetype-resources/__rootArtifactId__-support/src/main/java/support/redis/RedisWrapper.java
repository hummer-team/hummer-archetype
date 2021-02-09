package ${package}.support.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class RedisWrapper {
    private static final RedisBucketOp BUCKET_OP = new RedisBucketOp();
    private static final RedisHashOp HASH_OP = new RedisHashOp();
    private static final EventOp EVENT_OP = new EventOp();

    public static EventOp eventOp() {
        return EVENT_OP;
    }

    public static RedisHashOp hashOp() {
        return HASH_OP;
    }

    public static RedisBucketOp setOp() {
        return BUCKET_OP;
    }

    public static class EventOp extends BaseRedisClient {
        public void publish(String eventName, Object message, long timeout, TimeUnit unit) {
            publish(eventName, message, timeout, unit, l -> {

            }, e -> {
                log.error("send to redis fail event name {},cause "
                        , eventName, e);
            });
        }

        public void publish(String eventName
                , Object message
                , long timeout
                , TimeUnit unit
                , Consumer<Long> success
                , Consumer<Throwable> fail) {
            RFuture<Long> future = publishAsync(eventName, message);
            try {
                future.awaitUninterruptibly(timeout, unit);
                success.accept(future.getNow());
            } catch (Throwable e) {
                fail.accept(e);
            }
        }

        public RFuture<Long> publishAsync(String eventName, Object message) {
            return client().getTopic(eventName).publishAsync(message);
        }

        public <D> void subscribe(String eventName, Class<D> dClass, EventHandler<D> eventHandler) {
            client().getTopic(eventName).addListener(dClass, new MessageListener<D>() {
                @Override
                public void onMessage(CharSequence channel, D msg) {
                    eventHandler.doEvent(channel.toString(), msg);
                }
            });
        }

        public <D> void removeListener(String topicName, EventHandler<D> eventHandler) {
            client().getTopic(topicName).removeListener(new MessageListener<D>() {
                @Override
                public void onMessage(CharSequence channel, D msg) {
                    eventHandler.doEvent(channel.toString(), msg);
                }
            });
        }
    }

    public static class RedisBucketOp extends BaseRedisClient {

        public <D> void set(String key, D data, long ttl, TimeUnit timeUnit) {
            client().getBucket(key).set(data, ttl, timeUnit);
        }

        public <D> D get(String key) {
            RBucket<D> drBucket = client().getBucket(key);
            return drBucket.get();
        }

        @Override
        protected RedissonClient client() {
            return super.client();
        }
    }

    public static class RedisHashOp extends BaseRedisClient {

        public <D> void hset(String hashName, String key, D data, long ttl, TimeUnit timeUnit) {
            RMap<String, D> map = client().getMap(hashName);
            map.put(key, data);
            map.expire(ttl, timeUnit);
        }

        public <D> void hsets(String hashName, Map<String, D> map, long ttl, TimeUnit timeUnit) {
            RMap<String, D> tempMap = client().getMap(hashName);
            for (Map.Entry<String, D> entry : map.entrySet()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            tempMap.expire(ttl, timeUnit);
        }

        public <D> D hget(String hashName, String key) {
            RMap<String, D> tempMap = client().getMap(hashName);
            return tempMap.get(key);
        }

        public <D> Collection<D> hmget(String hashName, String key) {
            RMap<String, D> tempMap = client().getMap(hashName);
            return tempMap.values();
        }

        @Override
        protected RedissonClient client() {
            return super.client();
        }
    }
}

