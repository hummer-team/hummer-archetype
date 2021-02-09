package ${package}.domain.resource.name;

import ${package}.domain.context.ReqContext;

public abstract class ResourceNameStrategy {
    public String name(ReqContext reqContext) {
        return generateName(reqContext);
    }

    abstract String generateName(ReqContext reqContext);
}
