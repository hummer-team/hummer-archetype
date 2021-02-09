package ${package}.domain.event.handler;

import ${package}.domain.Constant;
import ${package}.domain.event.BlockActionEvent;
import ${package}.domain.event.BlockEvent;
import ${package}.support.cache.CacheWrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ApiBlacklistBlockEventHandler implements BlockEventHandler {
    @Override
    public void handler(BlockEvent event, BlockEnum blockEnum) {
        //add local cache and publish to redis
        BlockActionEvent event1 = CacheWrapper.<String, BlockActionEvent>localCache().get(event.getReqContext().getReqKey());
        if (event1 == null) {
            BlockActionEvent actionEvent = new BlockActionEvent();
            actionEvent.setHost(event.getReqContext().getHost());
            actionEvent.setReqPatch(event.getReqContext().getPath());
            actionEvent.setAction(blockEnum.ordinal());
            actionEvent.setCode(event.getCode());
            actionEvent.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            actionEvent.setProducerId(Constant.getSystemIdentityId2());

            CacheWrapper.<String, BlockActionEvent>localCache().put(event.getReqContext().getReqKey(), actionEvent);
            //publish to redis
            RedisBusPublishEventHandler.publishToRedis(event,actionEvent);
        } else {
            //update local already cache data
            event1.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            event1.setCode(event.getCode());
            CacheWrapper.<String, BlockActionEvent>localCache().put(event.getReqContext().getReqKey(), event1);

            //publish to redis
            BlockActionEvent actionEvent = new BlockActionEvent();
            actionEvent.setHost(event.getReqContext().getHost());
            actionEvent.setReqPatch(event.getReqContext().getPath());
            actionEvent.setAction(blockEnum.ordinal());
            actionEvent.setProducerId(Constant.getSystemIdentityId2());
            actionEvent.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            RedisBusPublishEventHandler.publishToRedis(event,actionEvent);
        }
    }
}
