package ${package}.domain.event.handler;

import com.alibaba.fastjson.JSON;
import ${package}.domain.event.BlockEvent;
import ${package}.support.mq.MQWrapper;
import io.elves.core.properties.ElvesProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockEventMQHandler implements BlockEventHandler {
    public static final BlockEventHandler INSTANCE = new BlockEventMQHandler();

    @Override
    public void handler(BlockEvent event, BlockEnum blockEnum) {
        if (ElvesProperties.valueOfBoolean("venom.guardian.log.block.event", "true")) {
            log.error("block event: {}", event);
        }

        if (MQWrapper.enable()) {
            MQWrapper.sendOneway("venom-block"
                    , "block"
                    , JSON.toJSONBytes(event));
        }
    }
}
