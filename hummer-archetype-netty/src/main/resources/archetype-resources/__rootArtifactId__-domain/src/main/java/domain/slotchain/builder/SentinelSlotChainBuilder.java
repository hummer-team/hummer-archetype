package ${package}.domain.slotchain.builder;

import com.alibaba.csp.sentinel.slotchain.DefaultProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotChain;
import com.alibaba.csp.sentinel.slotchain.SlotChainBuilder;
import com.alibaba.csp.sentinel.slots.block.flow.FlowSlot;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.csp.sentinel.slots.nodeselector.NodeSelectorSlot;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlot;
import com.alibaba.csp.sentinel.slots.system.SystemSlot;
import ${package}.domain.slots.blacklist.SentinelBlacklistSlot;
import ${package}.domain.slots.whitelist.SentinelWhitelistSlot;

/**
 * add all slot to chain.
 *
 * @author lee
 */
public class SentinelSlotChainBuilder implements SlotChainBuilder {
    /**
     * Build the processor slot chain.
     *
     * @return a processor slot that chain some slots together
     */
    @Override
    public ProcessorSlotChain build() {
        ProcessorSlotChain chain = new DefaultProcessorSlotChain();
        chain.addLast(new SentinelBlacklistSlot());
        chain.addLast(new SentinelWhitelistSlot());
        chain.addLast(new NodeSelectorSlot());
        chain.addLast(new ClusterBuilderSlot());
        chain.addLast(new StatisticSlot());
        chain.addLast(new SystemSlot());
        chain.addLast(new FlowSlot());
        //chain.addLast(new DegradeSlot());
        return chain;
    }
}
