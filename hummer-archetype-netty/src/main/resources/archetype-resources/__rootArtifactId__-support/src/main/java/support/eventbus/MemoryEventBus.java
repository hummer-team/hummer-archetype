package ${package}.support.eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.elves.core.properties.ElvesProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lee
 */
@Slf4j
public class MemoryEventBus {

    private static final AsyncEventBus BUS;

    static {
        ThreadFactory tf =
                new ThreadFactoryBuilder()
                        .setNameFormat("memoryBus" + "-%d")
                        .setDaemon(true)
                        .build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors()
                , Runtime.getRuntime().availableProcessors() * 4
                , 0L
                , TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(ElvesProperties.valueOfInteger("memory.bus.queue.max.size"
                , "20000"))
                , tf
                , getRejectHandler());
        BUS = new AsyncEventBus(executor, getSubscriberExceptionHandler());
    }

    public static <E> void publish(E event) {
        BUS.post(event);
    }

    public static <E> void publish(Collection<E> events) {
        BUS.post(events);
    }

    public static void registerEventListener(Object obj) {
        if (obj.getClass().getAnnotation(Bus.class) == null) {
            throw new RuntimeException(String.format("class %s no set @Bus", obj));
        }
        BUS.register(obj);
    }

    private static RejectedExecutionHandler getRejectHandler() {
        return new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.warn("thread pool thread filled,");
                r.run();
            }
        };
    }

    private static SubscriberExceptionHandler getSubscriberExceptionHandler() {
        return new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                log.error("bus has exception,event is {},exception ", context.getEvent(), exception);
            }
        };
    }
}
