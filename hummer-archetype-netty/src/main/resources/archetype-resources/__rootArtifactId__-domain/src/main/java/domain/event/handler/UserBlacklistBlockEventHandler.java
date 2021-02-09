package ${package}.domain.event.handler;

import ${package}.domain.Constant;
import ${package}.domain.event.BlockActionEvent;
import ${package}.domain.event.BlockEvent;
import ${package}.support.cache.CacheWrapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ${package}.domain.Constant.DEFAULT_MAX_LOCK_TARGET_MAP_SIZE;

@Slf4j
public class UserBlacklistBlockEventHandler implements BlockEventHandler {
    @Override
    public void handler(BlockEvent event, BlockEnum blockEnum) {
        //add local cache and publish to redis
        BlockActionEvent event1 = CacheWrapper.<String, BlockActionEvent>localCache().get(event.getReqContext().getReqKey());
        if (event1 == null) {
            BlockActionEvent actionEvent = new BlockActionEvent();
            actionEvent.setHost(event.getReqContext().getHost());
            actionEvent.setReqPatch(event.getReqContext().getPath());
            actionEvent.setAction(blockEnum.ordinal());
            Map<String, Integer> blockMap = new HashMap<>(1);
            blockMap.put(event.getBlockTarget(), event.getTimeWindow());
            actionEvent.setBlockTarget(blockMap);
            actionEvent.setRuleType(event.getRuleType());
            actionEvent.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            actionEvent.setCode(event.getCode());
            actionEvent.setProducerId(Constant.getSystemIdentityId2());
            CacheWrapper.<String, BlockActionEvent>localCache().put(event.getReqContext().getReqKey(), actionEvent);
            //publish to redis
            RedisBusPublishEventHandler.publishToRedis(event, actionEvent);
        } else {
            //update local cache data
            event1.putLockTarget(event.getBlockTarget()
                    , event.getTimeWindow()
                    , DEFAULT_MAX_LOCK_TARGET_MAP_SIZE);
            event1.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            event1.setCode(event.getCode());
            CacheWrapper.<String, BlockActionEvent>localCache().put(event.getReqContext().getReqKey(), event1);

            //publish to redis
            BlockActionEvent actionEvent = new BlockActionEvent();
            Map<String, Integer> blockMap = new HashMap<>(1);
            blockMap.put(event.getBlockTarget(), event.getTimeWindow());
            actionEvent.setBlockTarget(blockMap);
            actionEvent.setHost(event.getReqContext().getHost());
            actionEvent.setReqPatch(event.getReqContext().getPath());
            actionEvent.setAction(blockEnum.ordinal());
            actionEvent.setProducerId(Constant.getSystemIdentityId2());
            actionEvent.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            RedisBusPublishEventHandler.publishToRedis(event, actionEvent);
        }
    }
}
