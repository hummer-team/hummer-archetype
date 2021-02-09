package ${package}.config;

import com.alibaba.csp.sentinel.slots.system.SystemRule;
import ${package}.config.listener.SentinelConfig;
import lombok.Data;

import java.util.Map;

/**
 * application resource rule
 *
 * @author lee
 */
@Data
public class AppResourceRule {
    /**
     * app id。e.g:www.panli.com
     */
    private String appId;
    /**
     * sys rule
     */
    private SystemRule sysRule;
    /**
     * resource flow detail rule.
     */
    private SentinelConfig sentinelRule;
    /**
     * hot parameter detail rule,if current resource no detail rule then global rule
     */
    private Map<String, ParameterRule> resourceWhitelistRule;
    private Map<String, ParameterRule> resourceBlacklistRule;

}
