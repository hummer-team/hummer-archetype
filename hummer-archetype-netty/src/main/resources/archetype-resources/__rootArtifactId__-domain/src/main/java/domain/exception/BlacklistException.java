package ${package}.domain.exception;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class BlacklistException extends BlockException {

    public BlacklistException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public BlacklistException(String ruleLimitApp, AbstractRule rule) {
        super(ruleLimitApp, rule);
    }

    public BlacklistException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlacklistException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    public BlacklistException(String ruleLimitApp, String message, AbstractRule rule) {
        super(ruleLimitApp, message, rule);
    }

    public BlacklistException(String ruleLimitApp,Object... args) {
        super(ruleLimitApp,args);
    }

    public BlacklistException(String ruleLimitApp, AbstractRule rule,Object... args) {
        super(ruleLimitApp, rule,args);
    }

    public BlacklistException(String message, Throwable cause,Object... args) {
        super(message, cause,args);
    }

    public BlacklistException(String ruleLimitApp, String message,Object... args) {
        super(ruleLimitApp, message,args);
    }

    public BlacklistException(String ruleLimitApp, String message, AbstractRule rule,Object... args) {
        super(ruleLimitApp, message, rule,args);
    }
}
