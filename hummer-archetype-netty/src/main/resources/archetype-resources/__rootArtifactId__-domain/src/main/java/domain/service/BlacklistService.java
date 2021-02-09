package ${package}.domain.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.exception.BlacklistException;
import ${package}.config.AppResourceRule;
import ${package}.config.Parameter;
import ${package}.config.ParameterRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

import static ${package}.domain.Constant.BLACK_LOCK_CODE;
import static ${package}.config.Parameter.PARAMETER_COOKIE;
import static ${package}.config.Parameter.PARAMETER_IP;
import static ${package}.config.Parameter.PARAMETER_QUERY_STRING;
import static ${package}.config.Parameter.PARAMETER_UA;

@Slf4j
public class BlacklistService implements CheckService {
    public static final CheckService instance = new BlacklistService();

    private static void checkOfCookie(Parameter parameter, ReqContext reqContext, int timeWindow)
            throws BlacklistException {
        //todo
    }

    private static void checkOfUa(Parameter parameter, ReqContext reqContext, int timeWindow)
            throws BlacklistException {
        for (Map.Entry<String, String> parEntry : parameter.getParameter().entrySet()) {
            if (reqContext.getUserAgent().contains(parEntry.getKey())) {
                String lockTarget = LockTargetService.getLockTarget(parameter, reqContext);
                throw new BlacklistException(lockTarget, "reject request!!.", timeWindow, BLACK_LOCK_CODE);
            }
        }
    }

    private static void checkOfIp(Parameter parameter, ReqContext reqContext, int timeWindow)
            throws BlacklistException {

        checkIsAnyOfBlock(parameter, reqContext, timeWindow);

        for (Map.Entry<String, String> parEntry : parameter.getParameter().entrySet()) {
            if (parEntry.getKey().startsWith(reqContext.getClientIp())) {
                String lockTarget = LockTargetService.getLockTarget(parameter, reqContext);
                throw new BlacklistException(lockTarget, "reject request.", timeWindow, BLACK_LOCK_CODE);
            }
        }
    }

    private static void checkOfQs(Parameter parameter
            , ReqContext reqContext
            , int timeWindow) throws BlacklistException {
        if (MapUtils.isEmpty(reqContext.getQueryString())) {
            return;
        }

        checkIsAnyOfBlock(parameter, reqContext, timeWindow);

        for (Map.Entry<String, String> parEntry : parameter.getParameter().entrySet()) {
            if (reqContext.getQueryString().containsKey(parEntry.getKey())) {
                String lockTarget = LockTargetService.getLockTarget(parameter, reqContext);
                throw new BlacklistException(lockTarget, "reject request!!!.", timeWindow, BLACK_LOCK_CODE);
            }
        }
    }

    private static void checkIsAnyOfBlock(Parameter parameter
            , ReqContext reqContext
            , int timeWindow) throws BlacklistException {
        if (parameter.getParameter().containsKey("*")) {
            String lockTarget = LockTargetService.getLockTarget(parameter, reqContext);
            throw new BlacklistException(lockTarget, "reject request!!!.", timeWindow, BLACK_LOCK_CODE);
        }
    }


    @Override
    public void check(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        AppResourceRule rule = reqContext.getRule();
        if (MapUtils.isEmpty(rule.getResourceBlacklistRule())) {
            log.debug("request host {} no settings blacklist param,continue next slot", reqContext.getHost());
            return;
        }

        Map<String, ParameterRule> paramMap = rule.getResourceBlacklistRule();
        ParameterRule parameterRule = paramMap.get(reqContext.getPath());
        if (parameterRule == null) {
            log.debug("request host {} url {} no settings blacklist parameter rule,continue next slot"
                    , reqContext.getHost()
                    , reqContext.getPath());
            return;
        }

        Parameter ipParameter = parameterRule.getParameter().get(PARAMETER_IP);
        if (ipParameter != null) {
            checkOfIp(ipParameter, reqContext, parameterRule.getTimeWindow());
        }

        Parameter qsParameter = parameterRule.getParameter().get(PARAMETER_QUERY_STRING);
        if (qsParameter != null) {
            checkOfQs(qsParameter, reqContext, parameterRule.getTimeWindow());
        }

        Parameter uaParameter = parameterRule.getParameter().get(PARAMETER_UA);
        if (uaParameter != null) {
            checkOfUa(uaParameter, reqContext, parameterRule.getTimeWindow());
        }

        Parameter cookieParameter = parameterRule.getParameter().get(PARAMETER_COOKIE);
        if (cookieParameter != null) {
            checkOfCookie(cookieParameter, reqContext, parameterRule.getTimeWindow());
        }

        log.debug("request host {} path {} blacklist slot pass,enter next slot."
                , reqContext.getHost(), reqContext.getPath());
    }
}
