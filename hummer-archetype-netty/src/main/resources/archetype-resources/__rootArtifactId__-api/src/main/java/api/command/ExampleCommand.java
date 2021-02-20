package ${package}.api.command;

import io.elves.core.command.CommandActionMapping;
import io.elves.core.command.CommandHandlerMapping;
import io.elves.core.context.RequestContext;
import io.elves.core.request.HttpMethod;
import io.elves.core.response.CommandResponse;

import static io.elves.core.ElvesConstants.JSON_CODER;

@CommandHandlerMapping(name = "/v1")
public class ExampleCommand {
    /**
     * implement business process
     *
     * @param context request context
     * @return
     */
    @CommandActionMapping(name = "/hellword"
            , httpMethod = HttpMethod.GET
            , respEncoderType = JSON_CODER)
    public CommandResponse<String> viewRule(RequestContext context) {
        return CommandResponse.ok("hellword");
    }
}
