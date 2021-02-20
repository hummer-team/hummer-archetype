package ${package}.domain.event.handler;

import com.google.common.eventbus.Subscribe;
import ${package}.domain.event.DomainEvent;
import ${package}.support.eventbus.Bus;

@Bus
public class DomainEventHandler {
    @Subscribe
    public void handler(DomainEvent event) {
        //todo
    }
}
