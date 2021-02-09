package ${package}.domain.slots.blacklist;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.service.BlacklistService;
import ${package}.domain.slotchain.AbstractProcessorSlot;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlacklistSlot extends AbstractProcessorSlot<ResourceEntity> {
    /**
     * execute check,if failed then throw exception else call next slot
     *
     * @param reqContext
     * @param statistics
     * @throws Throwable
     */
    @Override
    public void entry(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        BlacklistService.instance.check(reqContext, resourceEntity);
        doNextSlot(reqContext, resourceEntity);
    }
}
