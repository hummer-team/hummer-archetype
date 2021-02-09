package ${package}.config.listener;

import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.Builder;
import lombok.Data;

/**
 * @author edz
 */
@Data
@Builder
public class ListenerEvent {
    private String dataId;
    private String groupId;
    private Listener listener;
    private SystemRule systemRule;
    private SentinelConfig sentinelConfig;
}
