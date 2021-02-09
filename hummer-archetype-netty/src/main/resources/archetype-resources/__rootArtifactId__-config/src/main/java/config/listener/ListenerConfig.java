package ${package}.config.listener;

import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ${package}.config.AppResourceRule;
import ${package}.config.repository.MemoryRuleRepository;
import ${package}.support.eventbus.MemoryEventBus;
import io.elves.common.util.StringUtil;
import io.elves.core.properties.ElvesProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class ListenerConfig {
    private static final ListenerConfig config = new ListenerConfig();
    private final ConcurrentHashMap<String, ListenerEvent> listenerMap = new ConcurrentHashMap<>();

    private ListenerConfig() {

    }

    public static ListenerConfig instance() {
        return config;
    }

    public void startListener() throws NacosException {
        String groupId = ElvesProperties.valueOfString("config.center.gateway.group.id", "DEFAULT_GROUP");
        long timeoutMillis = ElvesProperties.valueOfLong("config.center.gateway.timeout.millis", "5000");
        ConfigService configService = NacosFactory.createConfigService(getConfigServerProperties());
        String content = getRootData(configService, groupId, timeoutMillis);
        List<String> rootData = JSON.parseArray(content, String.class);
        if (CollectionUtils.isEmpty(rootData)) {
            log.warn("no venom config.");
            return;
        }
        log.debug("venom root group id is \n{}", rootData);
        addListenerForFirstLoading(rootData
                , groupId
                , timeoutMillis
                , configService);
    }

    private String getRootData(ConfigService configService, String groupId, long timeoutMillis) throws NacosException {
        return configService.getConfigAndSignListener(
                ElvesProperties.valueOfString("config.center.dataids"
                        , "venom-sh-root")
                , groupId
                , timeoutMillis
                , new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String s) {
                        try {
                            log.debug(">> receive config value \n{}", s);
                            updateListener(s, groupId, timeoutMillis, configService);
                        } catch (NacosException e) {
                            log.warn("reset gateway config failed", e);
                        }
                    }
                });
    }

    private void updateListener(String configVal, String groupId, long timeoutMillis, ConfigService configService)
            throws NacosException {
        if (StringUtil.isEmpty(configVal)) {
            MemoryRuleRepository.instance().deleteAll();
            for (Map.Entry<String, ListenerEvent> entry : listenerMap.entrySet()) {
                //remove nacos listener
                configService.removeListener(entry.getKey()
                        , entry.getValue().getGroupId()
                        , entry.getValue().getListener());
            }
            removeAllSentinelConfig();
        }

        //config key
        List<String> configRootDataIds = JSON.parseArray(configVal, String.class);
        if (CollectionUtils.isEmpty(configRootDataIds)) {
            log.warn("no configRootDataIds config.");
            return;
        }

        //add listener if not settings
        for (String dataId : configRootDataIds) {
            if (!listenerMap.containsKey(dataId)) {
                addListenerForReLoading(Collections.singletonList(dataId), groupId, timeoutMillis, configService);
                log.debug("add new listener,data id {} group id {}", dataId, groupId);
            }
        }

        //remove listener
        for (Map.Entry<String, ListenerEvent> entry : listenerMap.entrySet()) {
            if (!configRootDataIds.contains(entry.getKey())) {
                //remove rule
                MemoryRuleRepository.instance().deleteById(entry.getKey());
                //remove nacos listener
                configService.removeListener(entry.getKey()
                        , entry.getValue().getGroupId()
                        , entry.getValue().getListener());
                removeSentinelConfig(entry);
                log.debug("remove nacos listener ok,data id {} group id {}"
                        , entry.getKey()
                        , entry.getValue().getGroupId());
            }
        }
    }

    private void removeSentinelConfig(Map.Entry<String, ListenerEvent> entry) {
        if (entry.getValue() == null || entry.getValue().getSentinelConfig() == null) {
            return;
        }
        //remove DegradeRule
        if (CollectionUtils.isNotEmpty(entry.getValue().getSentinelConfig().getDegradeRule())) {
            List<DegradeRule> rules = DegradeRuleManager.getRules()
                    .stream()
                    .filter(f -> entry
                            .getValue()
                            .getSentinelConfig()
                            .getDegradeRule()
                            .stream()
                            .noneMatch(p -> p.getResource().equals(f.getResource())))
                    .collect(Collectors.toList());
            DegradeRuleManager.loadRules(rules);
        }

        //remove FlowRule
        if (CollectionUtils.isNotEmpty(entry.getValue().getSentinelConfig().getFlowRule())) {
            List<FlowRule> rules = FlowRuleManager.getRules()
                    .stream()
                    .filter(f -> entry
                            .getValue()
                            .getSentinelConfig()
                            .getFlowRule()
                            .stream()
                            .noneMatch(p -> p.getResource().equals(f.getResource())))
                    .collect(Collectors.toList());
            FlowRuleManager.loadRules(rules);
        }

        //reload sys rule.
        if (entry.getValue().getSystemRule() != null) {
            SystemRuleManager.loadRules(Collections.singletonList(entry.getValue().getSystemRule()));
        }
    }

    private void addListenerForReLoading(List<String> gatewayGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService) throws NacosException {
        addListener(gatewayGroup, groupId, timeoutMillis, configService, false);
    }

    private void addListenerForFirstLoading(List<String> gatewayGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService) throws NacosException {
        addListener(gatewayGroup, groupId, timeoutMillis, configService, true);
    }

    private void addListener(List<String> rootDataGroup
            , String groupId
            , long timeoutMillis
            , ConfigService configService
            , boolean isFirstLoading)
            throws NacosException {
        Set<DegradeRule> degradeRules = Sets.newConcurrentHashSet();
        Set<FlowRule> flowRules = Sets.newConcurrentHashSet();
        Set<SystemRule> systemRules = Sets.newConcurrentHashSet();
        //foreach all data id
        for (String dataId : rootDataGroup) {
            Listener listener = getListener();
            String configVal = configService.getConfigAndSignListener(dataId, groupId, timeoutMillis, listener);
            if (!Strings.isNullOrEmpty(configVal)) {
                //refresh gateway config
                log.debug("refresh config done,config value is \n{}", configVal);
                //serial to AppResourceRule instance
                AppResourceRule appResourceRule = JSON.parseObject(configVal, new TypeReference<AppResourceRule>() {
                });
                //refresh memory rule.
                refreshAppResourceRule(appResourceRule);
                //add to
                listenerMap.put(dataId
                        , ListenerEvent
                                .builder()
                                .dataId(dataId)
                                .groupId(groupId)
                                .listener(listener)
                                .systemRule(appResourceRule.getSysRule())
                                .sentinelConfig(appResourceRule.getSentinelRule())
                                .build());
                if (isFirstLoading) {
                    addSentinelConfigForInitLoad(degradeRules, flowRules, systemRules, appResourceRule);
                } else {
                    //refresh sentinel config
                    refreshSentinelConfig(appResourceRule);
                }
            }
        }
        firstLoadingSentinel(isFirstLoading, degradeRules, flowRules, systemRules);
    }

    private void addSentinelConfigForInitLoad(
            Set<DegradeRule> degradeRules
            , Set<FlowRule> flowRules
            , Set<SystemRule> systemRules
            , AppResourceRule appResourceRule) {

        if (!ElvesProperties.valueOfBoolean("csp.sentinel.enable", "true")) {
            return;
        }
        //if sentinel set null then clean memory all sentinel rul config.
        if (appResourceRule.getSentinelRule() == null) {
            cleanSentinelConfig(degradeRules, flowRules, systemRules);
            return;
        }

        //add detailed and  global rule
        if (CollectionUtils.isNotEmpty(appResourceRule.getSentinelRule().getDegradeRule())) {
            degradeRules.addAll(appResourceRule.getSentinelRule().getDegradeRule());
        }
        if (appResourceRule.getSentinelRule().getGlobalDegrade() != null) {
            degradeRules.add(appResourceRule.getSentinelRule().getGlobalDegrade());
        }

        if (CollectionUtils.isNotEmpty(appResourceRule.getSentinelRule().getFlowRule())) {
            flowRules.addAll(appResourceRule.getSentinelRule().getFlowRule());
        }
        if (appResourceRule.getSentinelRule().getGlobalFlowRule() != null) {
            flowRules.add(appResourceRule.getSentinelRule().getGlobalFlowRule());
        }

        //add sys rule
        if (appResourceRule.getSysRule() != null) {
            systemRules.add(appResourceRule.getSysRule());
        }
    }

    private void firstLoadingSentinel(boolean isFirstLoading
            , Set<DegradeRule> degradeRules
            , Set<FlowRule> flowRules
            , Set<SystemRule> systemRules) {

        if (!ElvesProperties.valueOfBoolean("csp.sentinel.enable", "true")) {
            return;
        }

        if (isFirstLoading) {
            DegradeRuleManager.loadRules(Lists.newArrayList(degradeRules));
            FlowRuleManager.loadRules(Lists.newArrayList(flowRules));
            SystemRuleManager.loadRules(Lists.newArrayList(systemRules));
        }
    }

    private void refreshSentinelConfig(AppResourceRule appResourceRule) {
        if (!ElvesProperties.valueOfBoolean("csp.sentinel.enable", "true")) {
            return;
        }

        Set<DegradeRule> tempDegradeRules = Sets.newConcurrentHashSet();
        Set<FlowRule> tempFlowRules = Sets.newConcurrentHashSet();
        Set<SystemRule> systemRules = Sets.newConcurrentHashSet();

        tempDegradeRules.addAll(DegradeRuleManager.getRules());
        tempFlowRules.addAll(FlowRuleManager.getRules());
        systemRules.addAll(SystemRuleManager.getRules());

        addSentinelConfigForUpdate(tempDegradeRules, tempFlowRules, systemRules, appResourceRule);

        DegradeRuleManager.loadRules(Lists.newArrayList(tempDegradeRules));
        FlowRuleManager.loadRules(Lists.newArrayList(tempFlowRules));
        SystemRuleManager.loadRules(Lists.newArrayList(systemRules));

    }

    private void addSentinelConfigForUpdate(Set<DegradeRule> degradeRules
            , Set<FlowRule> flowRules
            , Set<SystemRule> systemRules
            , AppResourceRule appResourceRule) {

        if (!ElvesProperties.valueOfBoolean("csp.sentinel.enable", "false")) {
            return;
        }

        if (appResourceRule.getSentinelRule() == null) {
            cleanSentinelConfig(degradeRules, flowRules, systemRules);
            return;
        }

        if (CollectionUtils.isNotEmpty(appResourceRule.getSentinelRule().getDegradeRule())) {
            Map<String, DegradeRule> degradeRuleMap = new ConcurrentHashMap<>(16);
            for (DegradeRule rule : degradeRules) {
                degradeRuleMap.put(rule.getResource(), rule);
            }
            for (DegradeRule rule : appResourceRule.getSentinelRule().getDegradeRule()) {
                degradeRuleMap.put(rule.getResource(), rule);
            }
            degradeRules.clear();
            degradeRules.addAll(degradeRuleMap.values());
        }
        if (CollectionUtils.isNotEmpty(appResourceRule.getSentinelRule().getFlowRule())) {
            Map<String, FlowRule> flowRuleMap = new ConcurrentHashMap<>(16);
            for (FlowRule rule : flowRules) {
                flowRuleMap.put(rule.getResource(), rule);
            }
            for (FlowRule rule : appResourceRule.getSentinelRule().getFlowRule()) {
                flowRuleMap.put(rule.getResource(), rule);
            }
            flowRules.clear();
            flowRules.addAll(flowRuleMap.values());
        }

        if (appResourceRule.getSysRule() != null) {
            Map<String, SystemRule> systemRuleMap = new ConcurrentHashMap<>(16);
            for (SystemRule api : systemRules) {
                systemRuleMap.put(api.getResource(), api);
            }

            systemRuleMap.put(appResourceRule.getSysRule().getResource()
                    , appResourceRule.getSysRule());
            systemRules.clear();
            systemRules.addAll(systemRuleMap.values());
        }
    }

    private void cleanSentinelConfig(Set<DegradeRule> degradeRules
            , Set<FlowRule> flowRules
            , Set<SystemRule> systemRules) {
        degradeRules.clear();
        flowRules.clear();
        systemRules.clear();
    }

    private void refreshAppResourceRule(AppResourceRule appResourceRule) {
        MemoryRuleRepository.instance().update(appResourceRule);
        MemoryEventBus.publish(appResourceRule);
    }

    private Listener getListener() {
        return new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                if (Strings.isNullOrEmpty(configInfo)) {
                    return;
                }
                log.debug("refresh config done,config value is \n{}", configInfo);
                //parse config instance
                AppResourceRule appResourceRule = JSON.parseObject(configInfo, new TypeReference<AppResourceRule>() {
                });
                //refresh gateway config
                refreshAppResourceRule(appResourceRule);
                //refresh sentinel config
                refreshSentinelConfig(appResourceRule);
            }
        };
    }

    private void removeAllSentinelConfig() {
        DegradeRuleManager.loadRules(Collections.emptyList());
        FlowRuleManager.loadRules(Collections.emptyList());
        AuthorityRuleManager.loadRules(Collections.emptyList());
        SystemRuleManager.loadRules(Collections.emptyList());
    }

    private Properties getConfigServerProperties() {
        Properties properties = new Properties();
        properties.put("namespace"
                , ElvesProperties.valueOfString("config-center.namespace"
                        , "venom-sh-service-panli-com"));
        properties.put("serverAddr"
                , ElvesProperties.valueOfStringWithAssertNotNull("config-center.server-addr"));
        return properties;
    }
}
