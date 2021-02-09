package ${package}.support.mq;

import io.elves.core.ElvesShutdownHook;
import io.elves.core.properties.ElvesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

@Slf4j
public class MQWrapper {
    private static final MqServer MQ_SERVER = new MqServer();

    private MQWrapper() {

    }

    public static boolean enable() {
        return MQ_SERVER.enable();
    }

    public static void sendOneway(String topic, String tag, byte[] body) {
        if (MQ_SERVER.enable()) {
            MQ_SERVER.sendOneway(topic, tag, body);
        }
    }

    static class MqServer {
        private DefaultMQProducer producer;

        public MqServer() {
            if (enable()) {
                producer = new DefaultMQProducer("venom-sh");
                producer.setSendMsgTimeout(ElvesProperties.valueOfInteger("rocket.mq.producer.sendtimeout.second"
                        , "3000"));
                producer.setNamesrvAddr(ElvesProperties.valueOfString("rocket.mq.server-addr", ""));
                try {
                    producer.start();
                } catch (Throwable e) {
                    log.error("start rocket mq failed", e);
                }

                ElvesShutdownHook.register(this::shutdown);
            }
        }

        public boolean enable() {
            return ElvesProperties.valueOfBoolean("rocket.mq.producer.enable", "true");
        }

        public void sendOneway(String topic, String tag, byte[] body) {
            Message message = new Message(topic, tag, body);
            try {
                producer.sendOneway(message);
            } catch (Throwable e) {
                log.error("send message to rocket mq fail", e);
            }
        }

        public void shutdown() {
            producer.shutdown();
        }
    }
}
