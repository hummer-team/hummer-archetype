package ${package}.domain.slots.sys;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.service.SystemService;
import ${package}.domain.slotchain.AbstractProcessorSlot;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemSlot extends AbstractProcessorSlot<ResourceEntity> {
    /**
     * execute check,if failed then throw exception else call next slot
     *
     * @param reqContext
     * @throws Throwable
     */
    @Override
    public void entry(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        SystemService.instance.check(reqContext, resourceEntity);
        doNextSlot(reqContext, resourceEntity);
    }

    /**
     * free current slot resource
     *
     * @param reqContext
     * @param rule
     */
    @Override
    public void exit(ReqContext reqContext) {
        doNextSlotExit(reqContext);
    }
}
