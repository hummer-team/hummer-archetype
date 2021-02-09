package ${package}.domain.exception;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class WhitelistException extends BlockException {


    public WhitelistException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public WhitelistException(String ruleLimitApp, AbstractRule rule) {
        super(ruleLimitApp, rule);
    }

    public WhitelistException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhitelistException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    public WhitelistException(String ruleLimitApp, String message, AbstractRule rule) {
        super(ruleLimitApp, message, rule);
    }

    public WhitelistException(String ruleLimitApp, Object... args) {
        super(ruleLimitApp, args);
    }

    public WhitelistException(String ruleLimitApp, AbstractRule rule, Object... args) {
        super(ruleLimitApp, rule, args);
    }

    public WhitelistException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public WhitelistException(String ruleLimitApp, String message, Object... args) {
        super(ruleLimitApp, message, args);
    }

    public WhitelistException(String ruleLimitApp, String message, AbstractRule rule, Object... args) {
        super(ruleLimitApp, message, rule, args);
    }
}
