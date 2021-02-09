package ${package}.domain.slotchain;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;

/**
 * implement business logic
 *
 * @param <T>
 * @author lee
 */
public interface ProcessorSlot<T> {
    /**
     * execute check,if failed then throw exception else call next slot
     *
     * @param reqContext
     * @throws Throwable
     */
    void entry(ReqContext reqContext, T statistics) throws BlockException;

    /**
     * free current slot resource
     *
     * @param reqContext
     * @param rule
     * @param args
     */
    default void exit(ReqContext reqContext) {
    }

    /**
     * define slot name
     *
     * @return
     */
    default String slotName() {
        return "";
    }
}
