package ${package}.config.repository;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.google.common.collect.Maps;
import ${package}.config.AppResourceRule;
import ${package}.config.Parameter;
import ${package}.config.ParameterRule;
import ${package}.config.listener.SentinelConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ${package}.config.Parameter.PARAMETER_QUERY_STRING;

/**
 * memory  rule
 *
 * @author lee
 */
@Slf4j
public class MemoryRuleRepository implements RuleRepository {
    private static final RuleRepository memoryRuleRepository = new MemoryRuleRepository();
    private static final ConcurrentHashMap<String, AppResourceRule>
            RULE_MAP = new ConcurrentHashMap<>();

    static {
        //ony test
        initTestRule();
    }

    public static RuleRepository instance() {
        return memoryRuleRepository;
    }

    private static void initTestRule() {
        AppResourceRule resourceRule = new AppResourceRule();
        resourceRule.setAppId("www.panli.com_test");

        //sys rule
        SystemRule sysRule = new SystemRule();
        sysRule.setMaxThread(20);
        sysRule.setHighestSystemLoad(1.2);
        sysRule.setHighestCpuUsage(0.85);
        resourceRule.setSysRule(sysRule);

        SentinelConfig sentinelConfig = new SentinelConfig();

        FlowRule globalFlowRule = new FlowRule();
        globalFlowRule.setCount(10000);
        globalFlowRule.setResource("/");
        sentinelConfig.setGlobalFlowRule(globalFlowRule);

        FlowRule flowRule1 = new FlowRule();
        flowRule1.setCount(10000);
        flowRule1.setResource("/a/b");

        FlowRule flowRule2 = new FlowRule();
        flowRule2.setCount(10000);
        flowRule2.setResource("/a/c");
        List<FlowRule> detailedFlowRule = new ArrayList<>(2);
        detailedFlowRule.add(flowRule2);
        detailedFlowRule.add(flowRule1);
        sentinelConfig.setFlowRule(detailedFlowRule);


        DegradeRule globalDegrade = new DegradeRule();
        globalDegrade.setResource("/");
        globalDegrade.setCount(10);
        sentinelConfig.setGlobalDegrade(globalDegrade);

        DegradeRule degrade1 = new DegradeRule();
        degrade1.setResource("/a/c");
        degrade1.setCount(10);

        DegradeRule degrade2 = new DegradeRule();
        degrade2.setResource("/d/f");
        degrade2.setCount(10);

        List<DegradeRule> detailedDegrade = new ArrayList<>(2);
        detailedDegrade.add(degrade2);
        detailedDegrade.add(degrade1);
        sentinelConfig.setDegradeRule(detailedDegrade);

        resourceRule.setSentinelRule(sentinelConfig);

        Map<String, ParameterRule> whitelistParamRule = Maps.newConcurrentMap();
        Map<String, Parameter> parameter = Maps.newConcurrentMap();

        Parameter parameter1 = new Parameter();
        Map<String, String> p1 = new HashMap<>();
        p1.put("a", "100");
        parameter1.setParameter(p1);
        parameter.put(PARAMETER_QUERY_STRING, parameter1);

        ParameterRule pRule = new ParameterRule();
        pRule.setParameter(parameter);
        whitelistParamRule.put("/", pRule);
        resourceRule.setResourceWhitelistRule(whitelistParamRule);


        Map<String, ParameterRule> blacklistParamRule = Maps.newConcurrentMap();
        Map<String, Parameter> parameterMap = Maps.newConcurrentMap();

        Parameter parameter2 = new Parameter();
        Map<String, String> p2 = new HashMap<>();
        p2.put("a", "100");
        parameter2.setParameter(p2);
        parameterMap.put(PARAMETER_QUERY_STRING, parameter2);


        ParameterRule pRule2 = new ParameterRule();
        pRule2.setParameter(parameterMap);
        blacklistParamRule.put("/", pRule2);
        resourceRule.setResourceBlacklistRule(blacklistParamRule);

        RULE_MAP.put("www.panli.com", resourceRule);
    }

    @Override
    public AppResourceRule getRule(String appId) {
        return RULE_MAP.get(appId);
    }

    @Override
    public void update(AppResourceRule rule) {
        RULE_MAP.put(rule.getAppId(), rule);
    }

    @Override
    public void deleteAll() {
        RULE_MAP.clear();
        log.debug("delete all rule map.");
    }

    @Override
    public void deleteById(String appId) {
        RULE_MAP.remove(appId);
    }

    @Override
    public int count() {
        return RULE_MAP.size();
    }

    @Override
    public List<String> keys() {
        Enumeration<String> enumeration = RULE_MAP.keys();
        List<String> keys = new ArrayList<>(16);
        for (Enumeration<String> e = enumeration; e.hasMoreElements(); ) {
            keys.add(e.nextElement());
        }
        return keys;
    }

    @Override
    public Collection<AppResourceRule> allRule() {
        return RULE_MAP.values();
    }
}
