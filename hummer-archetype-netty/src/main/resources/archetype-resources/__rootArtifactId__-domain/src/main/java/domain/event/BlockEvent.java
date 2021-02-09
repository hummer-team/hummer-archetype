package ${package}.domain.event;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockEvent {
    private BlockException exception;
    private int timeWindow;
    private String blockTarget;
    private String ruleType;
    private ReqContext reqContext;
    private Integer code;
}
