package ${package}.domain.slotchain;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lee
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiSlotOrder {
    /**
     * {@link Integer#MAX_VALUE}
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    /**
     * {@link Integer#MIN_VALUE;}
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * default by {@link #LOWEST_PRECEDENCE}
     */
    int value() default LOWEST_PRECEDENCE;
}
