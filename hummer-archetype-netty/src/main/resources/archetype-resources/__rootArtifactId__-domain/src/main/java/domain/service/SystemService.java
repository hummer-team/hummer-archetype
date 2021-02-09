package ${package}.domain.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;
import ${package}.domain.exception.SysBlockException;
import ${package}.domain.slots.sys.SystemStatistics;
import ${package}.config.AppResourceRule;
import ${package}.config.repository.MemoryRuleRepository;

import static ${package}.domain.Constant.AVAILABLE_PROCESSORS;
import static ${package}.domain.Constant.DEFAULT_LOCKED_TIME_WINDOW_SECOND;
import static ${package}.domain.Constant.LOCK_SYS_TARGET;
import static ${package}.domain.Constant.SYS_CODE;

public class SystemService implements CheckService {
    public static final CheckService instance = new SystemService();

    @Override
    public void check(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException {
        AppResourceRule rule = MemoryRuleRepository.instance().getRule("venom-sh.service.panli.com");

        if (SystemStatistics.getCurrentCpuUsage() >= rule.getSysRule().getHighestCpuUsage()
                || SystemStatistics.getCurrentSystemAvgLoad() >=
                Math.max(rule.getSysRule().getHighestSystemLoad(), AVAILABLE_PROCESSORS)) {

            throw new SysBlockException(LOCK_SYS_TARGET
                    , DEFAULT_LOCKED_TIME_WINDOW_SECOND * 3
                    , SYS_CODE);
        }
    }
}
