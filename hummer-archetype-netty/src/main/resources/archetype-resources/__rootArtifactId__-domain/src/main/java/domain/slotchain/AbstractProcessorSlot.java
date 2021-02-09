package ${package}.domain.slotchain;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;

/**
 * @author lee
 */
public abstract class AbstractProcessorSlot<T> implements ProcessorSlot<T> {
    private AbstractProcessorSlot<?> next = null;

    public void doNextSlot(ReqContext reqContext, Object param) throws BlockException {
        if (next != null) {
            next.transformEntry(reqContext, param);
        }
    }

    void transformEntry(ReqContext reqContext, Object param) throws BlockException {
        T t = (T) param;
        entry(reqContext, t);
    }

    public void doNextSlotExit(ReqContext reqContext) {
        if (next != null) {
            next.exit(reqContext);
        }
    }

    public AbstractProcessorSlot<?> getNext() {
        return next;
    }

    public void setNext(AbstractProcessorSlot<?> next) {
        this.next = next;
    }
}
