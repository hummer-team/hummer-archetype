package ${package}.domain.context;

import com.google.common.base.Objects;
import ${package}.config.AppResourceRule;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReqContext {
    private String host;
    private String path;
    private Map<String, String> queryString;
    private List<String> userAgent;
    private HttpHeaders header;
    private AppResourceRule rule;
    private String clientIp;

    public String getReqKey() {
        return String.format("%s%s", host, path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.host, this.path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReqContext)) {
            return false;
        }
        ReqContext r = (ReqContext) obj;
        return r.getHost().equalsIgnoreCase(this.host) && r.getPath().equalsIgnoreCase(this.path);
    }
}
