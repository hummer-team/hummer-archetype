package ${package}.domain.resource.name;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import ${package}.domain.context.ReqContext;
import ${package}.config.AppResourceRule;
import ${package}.config.listener.SentinelConfig;
import ${package}.config.repository.MemoryRuleRepository;
import org.apache.commons.collections.MapUtils;

public class ReqRateResourceNameStrategy extends ResourceNameStrategy {
    public static final ResourceNameStrategy instance = new ReqRateResourceNameStrategy();

    @Override
    String generateName(ReqContext reqContext) {
        AppResourceRule rule = MemoryRuleRepository.instance().getRule(reqContext.getHost());
        if (rule == null) {
            throw new IllegalArgumentException("no settings rule,please add.");
        }

        String resourceName = rule.getSentinelRule()
                .getFlowRule()
                .stream()
                .map(AbstractRule::getResource)
                .filter(f -> f.equals(reqContext.getPath()))
                .findFirst()
                .orElse("/");

        if (MapUtils.isEmpty(rule.getSentinelRule().getVenomFlowRule())) {
            return resourceName;
        }
        SentinelConfig.ReqObjLimitRate rate =
                rule.getSentinelRule()
                        .getVenomFlowRule()
                        .get(resourceName);
        if (rate != null && rate.getObjType().containsKey("ip")) {
            return String.format("%s:%s", resourceName, reqContext.getClientIp());
        }
        rate = rule.getSentinelRule()
                .getVenomFlowRule()
                .get("*");
        if (rate != null && rate.getObjType().containsKey("ip")) {
            return String.format("%s:%s", resourceName, reqContext.getClientIp());
        }

        return resourceName;
    }
}
