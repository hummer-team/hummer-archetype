package ${package}.config.listener;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SentinelConfig {
    private FlowRule globalFlowRule;
    private DegradeRule globalDegrade;
    private List<FlowRule> flowRule;
    private List<DegradeRule> degradeRule;
    private Map<String, ReqObjLimitRate> venomFlowRule;

    @Data
    public static class ReqObjLimitRate {
        private String ruleType = "flow";
        private int timeWindow;
        private Map<String, String> objType;
        private double count = 1;
    }
}
