package ${package}.domain.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.collect.Sets;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.exception.WhitelistException;
import ${package}.config.AppResourceRule;
import ${package}.config.Parameter;
import ${package}.config.ParameterRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Set;

import static ${package}.domain.Constant.WHITELIST_CODE;
import static ${package}.config.Parameter.ALL_MATCH;
import static ${package}.config.Parameter.ANY_MATCH;
import static ${package}.config.Parameter.PARAMETER_COOKIE;
import static ${package}.config.Parameter.PARAMETER_QUERY_STRING;
import static ${package}.config.Parameter.PARAMETER_UA;

@Slf4j
public class WhitelistService implements CheckService {
    public static final CheckService instance = new WhitelistService();

    private static void checkOfCookie(Parameter parameter, ReqContext reqContext, int timeWindow)
            throws WhitelistException {

    }

    private static void checkUa(Parameter parameter, ReqContext reqContext, int timeWindow)
            throws WhitelistException {

    }

    private static void checkQs(Parameter parameter, ReqContext reqContext, int timeWindow) throws WhitelistException {
        Set<Boolean> qsMatch = Sets.newConcurrentHashSet();

        for (Map.Entry<String, String> entry : parameter.getParameter().entrySet()) {
            String value = reqContext.getQueryString().get(entry.getKey());
            boolean isMatch = entry.getValue().equalsIgnoreCase(value);
            qsMatch.add(isMatch);
            if (isMatch && parameter.getMatchCondition() == ANY_MATCH) {
                return;
            }
        }

        if (parameter.getMatchCondition() == ALL_MATCH && qsMatch.contains(Boolean.FALSE)) {
            String locketTarget = LockTargetService.getLockTarget(parameter, reqContext);
            throw new WhitelistException(locketTarget, "reject request.", timeWindow, WHITELIST_CODE);
        }
    }

    @Override
    public void check(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        AppResourceRule rule = reqContext.getRule();
        if (MapUtils.isEmpty(rule.getResourceWhitelistRule())) {
            //
            log.debug("request host {} no settings Whitelist param,continue next slot", reqContext.getHost());
            return;
        }

        Map<String, ParameterRule> paramMap = rule.getResourceWhitelistRule();
        ParameterRule parameterRule = paramMap.get(reqContext.getPath());
        if (parameterRule == null) {
            log.debug("request host {} url {} no settings parameter rule,continue next slot"
                    , reqContext.getHost()
                    , reqContext.getPath());

            return;
        }

        Parameter qsParameter = parameterRule.getParameter().get(PARAMETER_QUERY_STRING);
        if (qsParameter != null) {
            checkQs(qsParameter, reqContext, parameterRule.getTimeWindow());
        }

        Parameter uaParameter = parameterRule.getParameter().get(PARAMETER_UA);
        if (uaParameter != null) {
            checkUa(uaParameter, reqContext, parameterRule.getTimeWindow());
        }

        Parameter cookieParameter = parameterRule.getParameter().get(PARAMETER_COOKIE);
        if (cookieParameter != null) {
            checkOfCookie(cookieParameter, reqContext, parameterRule.getTimeWindow());
        }

        log.debug("request host {} path {} whitelist slot pass,enter next slot"
                , reqContext.getHost(), reqContext.getPath());
    }
}
