package ${package}.domain.exception;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class SysBlockException extends BlockException {
    public SysBlockException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public SysBlockException(String ruleLimitApp, Object... args) {
        super(ruleLimitApp, args);
    }

    public SysBlockException(String ruleLimitApp, AbstractRule rule) {
        super(ruleLimitApp, rule);
    }

    public SysBlockException(String ruleLimitApp, AbstractRule rule, Object... args) {
        super(ruleLimitApp, rule, args);
    }

    public SysBlockException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public SysBlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public SysBlockException(String ruleLimitApp, String message, Object... args) {
        super(ruleLimitApp, message, args);
    }

    public SysBlockException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    public SysBlockException(String ruleLimitApp, String message, AbstractRule rule, Object... args) {
        super(ruleLimitApp, message, rule, args);
    }

    public SysBlockException(String ruleLimitApp, String message, AbstractRule rule) {
        super(ruleLimitApp, message, rule);
    }
}
