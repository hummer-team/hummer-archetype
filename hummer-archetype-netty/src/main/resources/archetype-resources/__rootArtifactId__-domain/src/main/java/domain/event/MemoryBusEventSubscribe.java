package ${package}.domain.event;

import com.google.common.eventbus.Subscribe;
import ${package}.domain.event.handler.BlockEnum;
import ${package}.domain.event.handler.BlockEventMQHandler;
import ${package}.domain.event.handler.EventContainer;
import ${package}.domain.resource.VenomEnter;
import ${package}.config.AppResourceRule;
import ${package}.config.listener.SentinelConfig;
import ${package}.support.eventbus.Bus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Bus
@Slf4j
public class MemoryBusEventSubscribe {

    @Subscribe
    public void handlerBlockEvent(BlockEvent event) {
        BlockEnum blockEnum = BlockEnum.getBlockType(event.getException());
        EventContainer.getHandler(blockEnum).handler(event, blockEnum);
    }

    @Subscribe
    public void handlerBlockEventToMq(BlockEvent event) {
        BlockEventMQHandler.INSTANCE.handler(event, null);
    }

    @Subscribe
    public void handlerRuleChangeEvent(AppResourceRule rule) {
        Map<String, SentinelConfig.ReqObjLimitRate> ruleMap = rule.getSentinelRule().getVenomFlowRule();
        if (ruleMap == null) {
            return;
        }

        for (Map.Entry<String, SentinelConfig.ReqObjLimitRate> entry : ruleMap.entrySet()) {
            VenomEnter.updateRate(rule.getAppId(), entry.getKey(), entry.getValue().getCount());
            log.debug("update {} - {} request limit rate {}"
                    , rule.getAppId(), entry.getKey(), entry.getValue().getCount());
        }
    }
}
