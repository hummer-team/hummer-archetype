package ${package}.api.command;

import ${package}.domain.Constant;
import ${package}.domain.event.MemoryBusEventSubscribe;
import ${package}.domain.event.RedisBusEventSubscribe;
import ${package}.domain.event.handler.EventContainer;
import ${package}.config.listener.ListenerConfig;
import ${package}.support.eventbus.MemoryEventBus;
import io.elves.core.command.CommandMapping;
import io.elves.core.context.RequestContext;
import io.elves.core.handle.CommandHandler;
import io.elves.core.response.CommandResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;

@CommandMapping(name = "/v1/api/root")
public class RootCommand implements CommandHandler<String> {
    /**
     * init command,this method is executed before {@link #handle(RequestContext)}
     */
    @SneakyThrows
    @Override
    public void init() {
        //config center
        ListenerConfig.instance().startListener();
        //register event handler service
        EventContainer.registerHandler();
        //register bus handler
        MemoryEventBus.registerEventListener(new MemoryBusEventSubscribe());
        //register redis event
        RedisBusEventSubscribe.subscribe();

        Constant.getSystemIdentityId2();
    }

    /**
     * implement business process
     *
     * @param context request context
     * @return
     */
    @Override
    public CommandResponse<String> handle(RequestContext context) {
       throw new NotImplementedException("not support");
    }

    /**
     * destroy resource
     */
    @Override
    public void destroy() {

    }
}
