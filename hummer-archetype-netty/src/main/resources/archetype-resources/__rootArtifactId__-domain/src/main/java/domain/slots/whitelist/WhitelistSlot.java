package ${package}.domain.slots.whitelist;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.service.WhitelistService;
import ${package}.domain.slotchain.AbstractProcessorSlot;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WhitelistSlot extends AbstractProcessorSlot<ResourceEntity> {
    /**
     * execute check,if failed then throw exception else call next slot
     *
     * @param reqContext
     * @throws Throwable
     */
    @Override
    public void entry(ReqContext reqContext,  ResourceEntity resourceEntity) throws BlockException {
        WhitelistService.instance.check(reqContext, resourceEntity);
        doNextSlot(reqContext,resourceEntity);
    }
}
