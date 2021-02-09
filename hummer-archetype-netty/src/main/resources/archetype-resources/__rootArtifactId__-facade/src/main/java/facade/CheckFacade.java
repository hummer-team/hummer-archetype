package ${package}.facade;


import ${package}.domain.context.CheckedResp;
import io.elves.core.context.RequestContext;

public interface CheckFacade {
    CheckedResp check(RequestContext requestContext);
}
