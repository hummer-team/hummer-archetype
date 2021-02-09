package ${package}.domain.resource;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.CheckedResp;
import ${package}.domain.context.ReqContext;
import ${package}.domain.event.BlockActionEvent;
import ${package}.domain.event.BlockEvent;
import ${package}.domain.event.handler.BlockEnum;
import ${package}.domain.resource.name.ReqRateResourceNameStrategy;
import ${package}.support.cache.CacheWrapper;
import ${package}.support.eventbus.MemoryEventBus;
import io.elves.core.properties.ElvesProperties;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static ${package}.domain.Constant.BLOCK_CODE;
import static ${package}.domain.Constant.DEFAULT_LOCKED_TIME_WINDOW_SECOND;
import static ${package}.domain.Constant.DEFAULT_RATELIMIT_DRIVER;
import static ${package}.domain.Constant.LOCK_SYS_TARGET;

/**
 * @author lee
 */
@Slf4j
public class ResourceEnter {
    private static final boolean USE_CSP = ElvesProperties.valueOfInteger("venom.guardian.ratelimit.driver.type"
            , DEFAULT_RATELIMIT_DRIVER) == 0;

    public static CheckedResp enter(ReqContext reqContext) {
        if (reqContext.getRule() == null) {
            return CheckedResp.builder().status(HttpResponseStatus.OK).coder(0).build();
        }

        CheckedResp checkedResp = advance(reqContext);
        if (checkedResp.getCoder() > 0) {
            return checkedResp;
        }

        if (USE_CSP) {
            return cspResourceEnter(reqContext);
        }

        return venomResourceEnter(reqContext);
    }


    private static CheckedResp venomResourceEnter(ReqContext reqContext) {
        try {
            return VenomEnter.enter(reqContext);
        } catch (BlockException e) {
            //send event
            MemoryEventBus.publish(BlockEvent.builder()
                    .exception(e)
                    .reqContext(reqContext)
                    .blockTarget(e.getRuleLimitApp())
                    .timeWindow(getTimeWindow(e))
                    .code(getBlockCode(e))
                    .build());
            //
            return CheckedResp.builder()
                    .status(HttpResponseStatus.BAD_REQUEST)
                    .coder(getBlockCode(e)).build();
        }
    }

    private static int getBlockCode(BlockException e) {
        if (e.getArgs() != null && e.getArgs().length > 1) {
            return (int) e.getArgs()[1];
        }
        return BLOCK_CODE;
    }

    private static CheckedResp cspResourceEnter(ReqContext reqContext) {
        try (Entry entry = SphU.entry(getRuleId(reqContext), EntryType.IN, 1, reqContext)) {
            return CheckedResp.builder().status(HttpResponseStatus.OK).build();
        } catch (BlockException e) {
            //send event
            MemoryEventBus.publish(BlockEvent.builder()
                    .exception(e)
                    .reqContext(reqContext)
                    .blockTarget(e.getRuleLimitApp())
                    .timeWindow(getTimeWindow(e))
                    .code(getBlockCode(e))
                    .build());
            //
            return CheckedResp.builder()
                    .status(HttpResponseStatus.BAD_REQUEST)
                    .coder(getBlockCode(e)).build();
        }
    }

    private static int getTimeWindow(BlockException e) {
        if (e.getArgs() == null || e.getArgs().length < 1) {
            return DEFAULT_LOCKED_TIME_WINDOW_SECOND;
        }
        try {
            return (int) e.getArgs()[0];
        } catch (Exception ex) {
            return DEFAULT_LOCKED_TIME_WINDOW_SECOND;
        }
    }

    public static CheckedResp advance(ReqContext reqContext) {

        BlockActionEvent event = CacheWrapper.<String, BlockActionEvent>localCache().get(LOCK_SYS_TARGET);
        //block all request
        if (event != null && Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .before(event.getAtExpired())) {
            return CheckedResp.builder()
                    .status(HttpResponseStatus.BAD_REQUEST)
                    .coder(event.getCode())
                    .build();
        }

        event = CacheWrapper.<String, BlockActionEvent>localCache().get(reqContext.getReqKey());
        if (event == null) {
            return CheckedResp.builder()
                    .coder(-1)
                    .build();
        }

        //lazy remove expired data for cache
        if (Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .after(event.getAtExpired())) {
            CacheWrapper.<String, BlockActionEvent>localCache().removeOf(reqContext.getReqKey());
            return CheckedResp.builder()
                    .coder(-1)
                    .build();
        }

        //block api
        BlockEnum blockEnum = BlockEnum.getBlockById(event.getAction());
        if (blockEnum == BlockEnum.BLOCK_API
                && Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .before(event.getAtExpired())) {
            if (log.isDebugEnabled()) {
                log.debug("{} -> {} , block api at Expired {}"
                        , reqContext.getHost()
                        , reqContext.getPath()
                        , DateFormat.getDateTimeInstance().format(event.getAtExpired()));
            }
            return CheckedResp.builder()
                    .coder(event.getCode())
                    .status(HttpResponseStatus.BAD_REQUEST).build();
        }

        //block ip
        Integer ipBlockTimeWindow = event.getBlockTarget().get(reqContext.getClientIp());
        if (log.isDebugEnabled()) {
            log.debug("{} -> {} , reject ip {} at Expired {}"
                    , reqContext.getHost()
                    , reqContext.getPath()
                    , reqContext.getClientIp()
                    , DateFormat.getDateTimeInstance().format(event.getAtExpired()));
        }
        if (ipBlockTimeWindow != null
                && Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .before(event.getAtExpired())) {
            return CheckedResp.builder()
                    .coder(event.getCode())
                    .status(HttpResponseStatus.BAD_REQUEST).build();
        }

        return CheckedResp.builder()
                .coder(-1)
                .build();
    }

    private static String getRuleId(ReqContext reqContext) {
        return ReqRateResourceNameStrategy.instance.name(reqContext);
    }
}
