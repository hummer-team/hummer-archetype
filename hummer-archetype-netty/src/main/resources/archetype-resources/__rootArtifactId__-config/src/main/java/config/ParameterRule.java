package ${package}.config;

import lombok.Data;

import java.util.Map;

@Data
public class ParameterRule {
    public static final int RULE_TYPE_WHITE_LIST = 0;
    public static final int RULE_TYPE_BLACK_LIST = 1;

    /**
     * parameter rule list
     */
    private Map<String,Parameter> parameter;
    /**
     * time unit second，if values ​​is -1 then always reject request
     */
    private int timeWindow = 60;
}
