package ${package}.domain.event;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class BlockActionEvent {
    private String producerId;
    private String host;
    private String reqPatch;
    private Integer action;
    private Map<String, Integer> blockTarget;
    private String ruleType;
    private Integer code;
    private Date atExpired;

    public void putLockTarget(String key, Integer timeWindow, int maxSize) {
        if (blockTarget == null) {
            blockTarget = new ConcurrentHashMap<>(16);
        }
        if (blockTarget.size() >= maxSize) {
            blockTarget.clear();
        }
        blockTarget.put(key, timeWindow);
    }

    public void putMapLockTarget(Map<String, Integer> targetMap, int maxSize) {
        if (blockTarget == null) {
            blockTarget = new ConcurrentHashMap<>(16);
        }
        if (blockTarget.size() >= maxSize) {
            blockTarget.clear();
        }
        blockTarget.putAll(targetMap);
    }
}
