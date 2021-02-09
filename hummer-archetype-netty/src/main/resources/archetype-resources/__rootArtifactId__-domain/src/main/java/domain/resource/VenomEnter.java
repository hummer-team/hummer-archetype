package ${package}.domain.resource;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.CheckedResp;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.slotchain.ProcessorSlotChain;
import ${package}.domain.slotchain.builder.DefaultSlotBuilder;
import ${package}.config.listener.SentinelConfig;
import io.elves.core.ElvesShutdownHook;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

import static ${package}.domain.Constant.DEFAULT_LIMIT_COUNT_PER_SECOND;
import static ${package}.domain.Constant.MAX_RESOURCE_SLOT;
import static ${package}.domain.Constant.ROOT_PATH;

@Slf4j
public class VenomEnter {
    private static final Object OBJ = new Object();
    private static ConcurrentHashMap<ReqContext, ResourceEntity>
            slotMap = new ConcurrentHashMap<>();

    static {
        ElvesShutdownHook.register(() -> slotMap.clear());
    }

    public static CheckedResp enter(ReqContext reqContext) throws BlockException {
        ResourceEntity resourceEntity = lookupSlot(reqContext);
        resourceEntity.getSlotChain().entry(reqContext, resourceEntity);
        return CheckedResp.builder().status(HttpResponseStatus.OK).build();
    }

    public static void updateRate(String host, String path, double rate) {
        slotMap.values().forEach(entity -> {
            if (entity.getPath().equals(String.format("%s:%s", host, path))) {
                entity.getLimiter().updateRate(rate);
            }
        });
    }

    private static ResourceEntity lookupSlot(ReqContext context) {
        ResourceEntity statistics = slotMap.get(context);
        if (statistics == null) {
            synchronized (OBJ) {
                statistics = slotMap.get(context);
                if (statistics == null) {
                    if (slotMap.size() >= MAX_RESOURCE_SLOT) {
                        log.warn("slotMap size > {},will clean this map", MAX_RESOURCE_SLOT);
                        slotMap.clear();
                    }
                    statistics = new ResourceEntity();
                    ProcessorSlotChain slotChain = DefaultSlotBuilder.instance.builder();
                    statistics.setSlotChain(slotChain);
                    statistics.setPath(String.format("%s:%s", context.getHost(), context.getPath()));
                    SentinelConfig.ReqObjLimitRate limitRate = context.getRule().getSentinelRule()
                            .getVenomFlowRule().get(context.getPath());
                    if (limitRate == null) {
                        limitRate = context.getRule().getSentinelRule()
                                .getVenomFlowRule().get(ROOT_PATH);
                    }
                    double count = limitRate == null ? DEFAULT_LIMIT_COUNT_PER_SECOND : limitRate.getCount();
                    statistics.setLimiter(new VenomRateLimiter(count));

                    ConcurrentHashMap<ReqContext, ResourceEntity>
                            tempMap = new ConcurrentHashMap<>(slotMap.size() + 1);
                    tempMap.putAll(slotMap);
                    tempMap.put(context, statistics);
                    slotMap = tempMap;
                }
            }
        }

        return statistics;
    }
}
