package ${package}.domain.slotchain;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;

/**
 * default slot chain implement
 *
 * @author lee
 */
public class DefaultProcessorSlotChain extends ProcessorSlotChain {

    AbstractProcessorSlot<?> first = new AbstractProcessorSlot<Object>() {
        @Override
        public void entry(ReqContext reqContext, Object param) throws BlockException {
            super.doNextSlot(reqContext, param);
        }

        @Override
        public void exit(ReqContext reqContext) {
            super.doNextSlotExit(reqContext);
        }
    };

    /**
     * point to head node
     */
    AbstractProcessorSlot<?> end = first;


    @Override
    public void entry(ReqContext reqContext, Object param) throws BlockException {
        first.transformEntry(reqContext, param);
    }

    @Override
    public void exit(ReqContext reqContext) {
        first.exit(reqContext);
    }

    @Override
    public AbstractProcessorSlot<?> getNext() {
        return first.getNext();
    }

    @Override
    public void setNext(AbstractProcessorSlot<?> next) {
       addLast(next);
    }

    /**
     * Add a processor to the head of this slot chain.
     *
     * @param slot processor to be added.
     */
    @Override
    public ProcessorSlotChain addFirst(AbstractProcessorSlot<?> slot) {
        slot.setNext(first.getNext());
        first.setNext(slot);
        if (end == first) {
            end = slot;
        }
        return this;
    }

    /**
     * Add a processor to the tail of this slot chain.
     *
     * @param slot processor to be added.
     */
    @Override
    public ProcessorSlotChain addLast(AbstractProcessorSlot<?> slot) {
        end.setNext(slot);
        end = slot;
        return this;
    }
}
