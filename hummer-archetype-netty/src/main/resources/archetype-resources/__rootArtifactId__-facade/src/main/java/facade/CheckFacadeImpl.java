package ${package}.facade;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import ${package}.domain.context.CheckedResp;
import ${package}.domain.context.ReqContext;
import ${package}.domain.resource.ResourceEnter;
import ${package}.config.repository.MemoryRuleRepository;
import ${package}.support.utils.IpUtil;
import io.elves.core.context.RequestContext;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class CheckFacadeImpl implements CheckFacade {

    public static final CheckFacade INSTANCE = new CheckFacadeImpl();

    @Override
    public CheckedResp check(RequestContext requestContext) {
        URI uri = URI.create(requestContext.getParam("url"));
        Map<String, String> queryStringMap = getQueryStringMap(uri);
        return ResourceEnter.enter(ReqContext.builder()
                .host(uri.getHost())
                .path(Strings.isNullOrEmpty(uri.getPath()) ? "/" : uri.getPath())
                .queryString(queryStringMap)
                .userAgent(requestContext.getHeaders().getAllAsString("User-Agent"))
                .clientIp(IpUtil.getRemoteAddr(requestContext.getHeaders()))
                .rule(MemoryRuleRepository.instance().getRule(uri.getHost()))
                .build());
    }

    private Map<String, String> getQueryStringMap(URI uri) {
        if (uri.getQuery() == null) {
            return Collections.emptyMap();
        }
        return Splitter.on("&").withKeyValueSeparator("=").split(uri.getQuery());
    }
}
