package ${package}.config.repository;

import ${package}.config.AppResourceRule;

import java.util.Collection;
import java.util.List;

public interface RuleRepository {
    AppResourceRule getRule(String appId);
    void update(AppResourceRule rule);
    void deleteAll();
    void deleteById(String appId);
    int count();
    List<String> keys();
    Collection<AppResourceRule> allRule();
}
