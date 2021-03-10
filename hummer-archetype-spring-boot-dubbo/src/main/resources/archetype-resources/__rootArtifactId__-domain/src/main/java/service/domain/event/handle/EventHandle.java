package ${package}.service.domain.event.handle;

import  ${package}.service.domain.event.BaseEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Slf4j
public class EventHandle {

    @EventListener
    public void handle(@NotNull BaseEvent event) {
        log.debug("handle vent", event);
    }
}