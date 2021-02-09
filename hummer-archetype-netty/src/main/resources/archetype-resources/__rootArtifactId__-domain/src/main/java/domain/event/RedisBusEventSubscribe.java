package ${package}.domain.event;

import ${package}.domain.Constant;
import ${package}.domain.event.handler.RedisBusSubscribeEventHandler;
import ${package}.support.redis.EventHandler;
import ${package}.support.redis.RedisWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisBusEventSubscribe {
    private RedisBusEventSubscribe() {

    }

    public static void subscribe() {
        RedisWrapper.eventOp().subscribe("venmo"
                , BlockActionEvent.class, new EventHandler<BlockActionEvent>() {
                    @Override
                    public void doEvent(String channel, BlockActionEvent blockActionEvent) {
                        if (log.isDebugEnabled()) {
                            log.debug("received channel :{} - Producer :{} - message :{}"
                                    , channel
                                    , blockActionEvent.getProducerId()
                                    , blockActionEvent);
                        }
                        if (blockActionEvent.getProducerId()
                                .equals(Constant.getSystemIdentityId2())) {
                            return;
                        }
                        RedisBusSubscribeEventHandler.handler(channel, blockActionEvent);
                    }
                });
    }
}
