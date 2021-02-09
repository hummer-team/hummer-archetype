package ${package}.config;

import lombok.Data;

import java.util.Map;

@Data
public class Parameter {
    public static final String PARAMETER_QUERY_STRING = "qs";
    public static final String PARAMETER_UA = "ua";
    public static final String PARAMETER_COOKIE = "cookie";
    public static final String PARAMETER_IP = "ip";
    public static final int ANY_MATCH = 0;
    public static final int ALL_MATCH = 1;
    /**
     * parameter type , e.g：queryString，ua，cookie
     */
    private String parameterType;
    private Map<String, String> parameter;
    /**
     * 0：any match；1：all match
     */
    private int matchCondition = 0;
    /**
     * The smaller the value, the higher the priority
     */
    private String lockTarget = "ip";
}
