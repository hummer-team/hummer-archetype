package ${package}.domain.exception;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class SimpleFlowException extends BlockException {
    public SimpleFlowException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public SimpleFlowException(String ruleLimitApp, Object... args) {
        super(ruleLimitApp, args);
    }

    public SimpleFlowException(String ruleLimitApp, AbstractRule rule) {
        super(ruleLimitApp, rule);
    }

    public SimpleFlowException(String ruleLimitApp, AbstractRule rule, Object... args) {
        super(ruleLimitApp, rule, args);
    }

    public SimpleFlowException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public SimpleFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleFlowException(String ruleLimitApp, String message, Object... args) {
        super(ruleLimitApp, message, args);
    }

    public SimpleFlowException(String ruleLimitApp, String message) {
        super(ruleLimitApp, message);
    }

    public SimpleFlowException(String ruleLimitApp, String message, AbstractRule rule, Object... args) {
        super(ruleLimitApp, message, rule, args);
    }

    public SimpleFlowException(String ruleLimitApp, String message, AbstractRule rule) {
        super(ruleLimitApp, message, rule);
    }
}
