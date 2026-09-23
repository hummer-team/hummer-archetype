package ${package}.service.domain.event.handle;

import  ${package}.service.domain.event.BaseEvent;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author guo.li
 */
@Component
@Slf4j
public class EventHandle {
    @EventListener
    public void handle(@NotNull BaseEvent event) {
        log.debug("handle vent {}", event);
    }
}