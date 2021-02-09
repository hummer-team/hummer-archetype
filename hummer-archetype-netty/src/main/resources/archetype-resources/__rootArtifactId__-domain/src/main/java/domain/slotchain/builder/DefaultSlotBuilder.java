package ${package}.domain.slotchain.builder;

import ${package}.domain.slotchain.DefaultProcessorSlotChain;
import ${package}.domain.slotchain.ProcessorSlotChain;
import ${package}.domain.slots.blacklist.BlacklistSlot;
import ${package}.domain.slots.flow.SimpleFlowSlot;
import ${package}.domain.slots.sys.SystemSlot;
import ${package}.domain.slots.whitelist.WhitelistSlot;

public class DefaultSlotBuilder implements SimpleSlotChainBuilder {
    public static final SimpleSlotChainBuilder instance = new DefaultSlotBuilder();

    @Override
    public ProcessorSlotChain builder() {
        DefaultProcessorSlotChain slotChain = new DefaultProcessorSlotChain();
        slotChain.addLast(new SystemSlot());
        slotChain.addLast(new BlacklistSlot());
        slotChain.addLast(new WhitelistSlot());
        slotChain.addLast(new SimpleFlowSlot());
        return slotChain;
    }
}
