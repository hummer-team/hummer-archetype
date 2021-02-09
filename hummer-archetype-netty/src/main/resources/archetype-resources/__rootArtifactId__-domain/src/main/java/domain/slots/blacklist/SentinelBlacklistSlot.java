package ${package}.domain.slots.blacklist;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import ${package}.domain.context.ReqContext;
import ${package}.domain.service.BlacklistService;
import lombok.extern.slf4j.Slf4j;

/**
 * request resource blacklist rule check
 *
 * @author lee
 */
@Slf4j
public class SentinelBlacklistSlot extends AbstractLinkedProcessorSlot<DefaultNode> {


    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper
            , DefaultNode defaultNode, int i, boolean b, Object... objects) throws Throwable {
        BlacklistService.instance.check(((ReqContext) objects[0]), null);
        fireEntry(context, resourceWrapper, defaultNode, i, b, objects);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int i, Object... objects) {
        //fireExit(context, resourceWrapper, i, objects);
    }
}
