package ${package}.domain.slotchain;

/**
 * this class manager all slot.
 *
 * @author lee
 */
public abstract class ProcessorSlotChain extends AbstractProcessorSlot<Object> {
    /**
     * Add a processor to the head of this slot chain.
     *
     * @param protocolProcessor processor to be added.
     */
    public abstract ProcessorSlotChain addFirst(AbstractProcessorSlot<?> protocolProcessor);

    /**
     * Add a processor to the tail of this slot chain.
     *
     * @param protocolProcessor processor to be added.
     */
    public abstract ProcessorSlotChain addLast(AbstractProcessorSlot<?> protocolProcessor);
}
