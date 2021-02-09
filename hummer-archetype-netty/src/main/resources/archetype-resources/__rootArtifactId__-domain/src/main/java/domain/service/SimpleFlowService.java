package ${package}.domain.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;

import ${package}.domain.context.ResourceEntity;
import ${package}.domain.exception.SimpleFlowException;
import lombok.extern.slf4j.Slf4j;

import static ${package}.domain.Constant.FLOW_BLOCK_CODE;

@Slf4j
public class SimpleFlowService implements CheckService {
    public static final CheckService instance = new SimpleFlowService();

    @Override
    public void check(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        boolean acquire = resourceEntity.getLimiter().tryAcquire();
        if (!acquire) {
            throw new SimpleFlowException(LockTargetService
                    .getLockTarget(reqContext)
                    , LockTargetService.getTimeWindow(reqContext),FLOW_BLOCK_CODE);
        }
        log.debug("request host {} path {} simple flow slot pass,enter next slot."
                , reqContext.getHost(), reqContext.getPath());
    }
}
