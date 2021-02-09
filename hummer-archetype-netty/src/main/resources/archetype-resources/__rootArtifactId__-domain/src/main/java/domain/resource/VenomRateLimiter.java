package ${package}.domain.resource;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

public class VenomRateLimiter {
    private RateLimiter rateLimiter;

    public VenomRateLimiter(double rate) {
        rateLimiter = RateLimiter.create(rate);
    }

    public VenomRateLimiter(double rate, long warmupPeriod, TimeUnit unit) {
        rateLimiter = RateLimiter.create(rate, warmupPeriod, unit);
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public boolean tryAcquire(long timeoutMills) {
        return tryAcquire(1, timeoutMills);
    }

    public boolean tryAcquire(int permits) {
        try {
            return rateLimiter.tryAcquire(permits);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean tryAcquire(int permits, long timeoutMills) {
        try {
            return rateLimiter.tryAcquire(permits, timeoutMills, TimeUnit.MILLISECONDS);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void updateRate(double rate) {
        rateLimiter.setRate(rate);
    }
}
