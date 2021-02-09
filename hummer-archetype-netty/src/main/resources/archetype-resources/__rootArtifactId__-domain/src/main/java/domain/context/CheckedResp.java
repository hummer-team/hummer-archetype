package ${package}.domain.context;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckedResp {
    private transient HttpResponseStatus status;
    private int coder;
}
