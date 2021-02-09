package ${package}.domain.event.handler;

import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import ${package}.domain.event.BlockActionEvent;
import ${package}.domain.event.BlockEvent;
import ${package}.support.redis.RedisWrapper;

import java.util.concurrent.TimeUnit;

import static ${package}.domain.Constant.PUBLISH_TO_REDIS_TIMEOUT_MILLS;

public class RedisBusPublishEventHandler {
    private RedisBusPublishEventHandler() {

    }

    public static void publishToRedis(BlockEvent event, BlockActionEvent actionEvent) {
        //publish to redis
        if (!(event.getException() instanceof FlowException)) {
            RedisWrapper.eventOp().publish(event.getReqContext().getHost(), actionEvent
                    , PUBLISH_TO_REDIS_TIMEOUT_MILLS
                    , TimeUnit.MILLISECONDS);
        }
    }
}
