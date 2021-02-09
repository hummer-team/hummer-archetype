package ${package}.test.slots;

import com.google.common.eventbus.Subscribe;
import ${package}.support.eventbus.Bus;
import ${package}.support.eventbus.MemoryEventBus;
import ${package}.support.redis.EventHandler;
import ${package}.support.redis.RedisWrapper;
import io.elves.core.properties.ElvesProperties;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RedisOpTest {
    private CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void before() throws IOException {
        ElvesProperties.load("test");
        Thread thread = new Thread(() -> {
            RedisWrapper.eventOp().subscribe("order", Event.class, new EventHandler<Event>() {
                @Override
                public void doEvent(String channel, Event event) {
                    System.out.println(channel + " receive event " + event);
                    latch.countDown();
                }
            });
        });
        thread.start();
    }

    @Test
    public void setString() {
        RedisWrapper.setOp().set("sdsd", 123, 5, TimeUnit.HOURS);
        System.out.println((Integer) RedisWrapper.setOp().get("sdsd"));
    }

    @Test
    public void hashOp() {
        RedisWrapper.hashOp().hset("myHash", "h1", "ddddd", 1, TimeUnit.HOURS);
        String v = RedisWrapper.hashOp().hget("myHash", "h1");
        Assert.assertEquals("ddddd", v);
    }

    @Test
    public void publish() throws InterruptedException {
        Event e = new Event();
        e.setMessage("hhhhh");
        RedisWrapper.eventOp().publish("order", e,100,TimeUnit.MILLISECONDS);
        e.setMessage("message 01");
        RedisWrapper.eventOp().publish("order", e,100,TimeUnit.MILLISECONDS);
        latch.await();
    }

    @Test
    public void bus() {
        MemoryEventBus.registerEventListener(new HandlerEvent());
        Event event = new Event();
        event.setMessage("bus");
        MemoryEventBus.publish(event);
    }


    @Bus
    public static class HandlerEvent {
        @Subscribe
        public void handler(Event e) {
            System.out.println("receive" + e + " thread: " + Thread.currentThread().getName());
        }
    }

    @Data
    public static class Event implements Serializable {
        private static final long serialVersionUID = -2567556748890597305L;
        private String message;
    }
}
