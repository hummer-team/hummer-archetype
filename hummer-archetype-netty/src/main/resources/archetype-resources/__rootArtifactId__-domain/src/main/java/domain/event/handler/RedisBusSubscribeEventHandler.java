package ${package}.domain.event.handler;

import ${package}.domain.event.BlockActionEvent;
import ${package}.support.cache.CacheWrapper;
import lombok.extern.slf4j.Slf4j;

import static ${package}.domain.Constant.DEFAULT_MAX_LOCK_TARGET_MAP_SIZE;

@Slf4j
public class RedisBusSubscribeEventHandler {
    private RedisBusSubscribeEventHandler() {

    }

    public static void handler(String channel, BlockActionEvent blockActionEvent) {
        BlockEnum blockEnum = BlockEnum.getBlockById(blockActionEvent.getAction());
        log.debug("received redis event block action {}", blockEnum);
        String key = String.format("%s%s", blockActionEvent.getHost(), blockActionEvent.getReqPatch());
        BlockActionEvent cacheEvent = CacheWrapper.<String, BlockActionEvent>localCache().get(key);
        if (cacheEvent == null) {
            CacheWrapper.<String, BlockActionEvent>localCache().put(key, blockActionEvent);
        } else {
            cacheEvent.setAtExpired(blockActionEvent.getAtExpired());
            cacheEvent.putMapLockTarget(blockActionEvent.getBlockTarget()
                    , DEFAULT_MAX_LOCK_TARGET_MAP_SIZE);
        }
    }
}
