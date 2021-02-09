package ${package}.domain.service;

import com.google.common.base.Strings;
import ${package}.domain.context.ReqContext;
import ${package}.config.Parameter;
import ${package}.config.listener.SentinelConfig;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

import static ${package}.domain.Constant.DEFAULT_LOCKED_TIME_WINDOW_SECOND;
import static ${package}.domain.Constant.LOCK_TARGET_IP;

public class LockTargetService {
    private LockTargetService() {

    }

    public static String getLockTarget(Parameter parameter, ReqContext reqContext) {
        if (Strings.isNullOrEmpty(parameter.getLockTarget())
                || LOCK_TARGET_IP.equalsIgnoreCase(parameter.getLockTarget())) {
            return reqContext.getClientIp();
        }
        return reqContext.getClientIp();
    }

    public static SentinelConfig.ReqObjLimitRate getReqObjLimitRate(ReqContext reqContext) {
        Map<String, SentinelConfig.ReqObjLimitRate> rateMap =
                reqContext.getRule().getSentinelRule().getVenomFlowRule();
        if (MapUtils.isEmpty(rateMap)) {
            return null;
        }

        SentinelConfig.ReqObjLimitRate rate = rateMap.get(reqContext.getPath());
        if (rate != null) {
            return rate;
        }
        return rateMap.get("*");
    }

    public static String getLockTarget(ReqContext reqContext) {
        SentinelConfig.ReqObjLimitRate rate = getReqObjLimitRate(reqContext);
        if (rate == null) {
            return reqContext.getClientIp();
        }

        if (rate.getObjType().containsKey(LOCK_TARGET_IP)) {
            return reqContext.getClientIp();
        }

        return reqContext.getPath();
    }

    public static int getTimeWindow(ReqContext reqContext) {
        SentinelConfig.ReqObjLimitRate limitRate = LockTargetService.getReqObjLimitRate(reqContext);
        int timeWindow = DEFAULT_LOCKED_TIME_WINDOW_SECOND;
        if (limitRate != null) {
            timeWindow = limitRate.getTimeWindow();
        }

        return timeWindow;
    }
}
