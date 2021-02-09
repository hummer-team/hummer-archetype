package ${package}.config;

import lombok.Data;

@Data
public class FlowRule {
    private String resourceName;
    private int rate;
    private int action;
    private int timeWindow;
    private int priority;
}
