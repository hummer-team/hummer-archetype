package ${package}.domain.slots.flow;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.service.SimpleFlowService;
import ${package}.domain.slotchain.AbstractProcessorSlot;

public class SimpleFlowSlot extends AbstractProcessorSlot<ResourceEntity> {

    /**
     * execute check,if failed then throw exception else call next slot
     *
     * @param reqContext
     * @param param
     * @throws Throwable
     */
    @Override
    public void entry(ReqContext reqContext, ResourceEntity statistics) throws BlockException {
        SimpleFlowService.instance.check(reqContext,statistics);
        doNextSlot(reqContext,statistics);
    }
}
