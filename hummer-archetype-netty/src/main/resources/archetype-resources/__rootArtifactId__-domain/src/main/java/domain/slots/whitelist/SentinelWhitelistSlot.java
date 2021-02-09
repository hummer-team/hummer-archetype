package ${package}.domain.slots.whitelist;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import ${package}.domain.context.ReqContext;
import ${package}.domain.service.WhitelistService;
import lombok.extern.slf4j.Slf4j;

/**
 * request resource whitelist rule check
 *
 * @author lee
 */
@Slf4j
public class SentinelWhitelistSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count, boolean prioritized
            , Object... args) throws Throwable {
        WhitelistService.instance.check(((ReqContext) args[0]), null);

        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        //fireExit(context, resourceWrapper, count, args);
    }
}
