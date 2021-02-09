package ${package}.domain.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.context.ReqContext;
import ${package}.domain.context.ResourceEntity;


public interface CheckService {
    void check(ReqContext reqContext, ResourceEntity resourceEntity) throws BlockException;
}
