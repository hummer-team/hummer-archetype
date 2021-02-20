package ${package}.api.command;

import ${package}.domain.event.handler.DomainEventHandler;
import ${package}.rule.listener.ListenerConfig;
import ${package}.support.eventbus.MemoryEventBus;
import io.elves.core.scope.ElvesScopeAction;
import io.elves.core.scope.ElvesScopeApplicationContext;
import io.elves.core.scope.ElvesScopeContext;
import io.elves.core.scope.ElvesScopeInit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ElvesScopeContext
@Slf4j
public class ExampleCommandInit {
    /**
     * init command,this method is executed before {@link io.elves.core.context.RequestContext}
     */
    @ElvesScopeAction
    public void init() {
        ElvesScopeApplicationContext.register(() -> new ElvesScopeInit() {

            @SneakyThrows
            @Override
            public void postconstruct() {
                //config center
                ListenerConfig.instance().startListener();
                //register event handler service
                //register bus handler
                MemoryEventBus.registerEventListener(new DomainEventHandler());
                log.debug("command init done.");
            }

            @Override
            public void destroy() {

            }

            @Override
            public int sort() {
                return 0;
            }
        });
    }
}
