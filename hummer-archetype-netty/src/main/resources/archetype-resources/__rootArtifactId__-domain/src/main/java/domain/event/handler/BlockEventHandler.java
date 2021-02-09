package ${package}.domain.event.handler;

import ${package}.domain.event.BlockEvent;

public interface BlockEventHandler {
    void handler(BlockEvent event, BlockEnum blockEnum);
}
