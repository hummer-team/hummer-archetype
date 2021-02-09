package ${package}.domain.slots.sys;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

import static ${package}.domain.Constant.AVAILABLE_PROCESSORS;

/**
 * @author lee
 */
@Slf4j
public class SystemStatusListener implements Runnable {

    volatile double currentLoad = -1;
    volatile double currentCpuUsage = -1;

    volatile String reason = "";

    volatile long processCpuTime = 0;
    volatile long processUpTime = 0;

    public double getSystemAverageLoad() {
        return currentLoad;
    }

    public double getCpuUsage() {
        return currentCpuUsage;
    }

    @Override
    public void run() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            currentLoad = osBean.getSystemLoadAverage();

            /*
             * Java Doc copied from {@link OperatingSystemMXBean#getSystemCpuLoad()}:</br>
             * Returns the "recent cpu usage" for the whole system. This value is a double in the [0.0,1.0] interval.
             * A value of 0.0 means that all CPUs were idle during the recent period of time observed, while a value
             * of 1.0 means that all CPUs were actively running 100% of the time during the recent period being
             * observed. All values between 0.0 and 1.0 are possible depending of the activities going on in the
             * system. If the system recent cpu usage is not available, the method returns a negative value.
             */
            double systemCpuUsage = osBean.getSystemCpuLoad();

            // calculate process cpu usage to support application running in container environment
            RuntimeMXBean runtimeBean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
            long newProcessCpuTime = osBean.getProcessCpuTime();
            long newProcessUpTime = runtimeBean.getUptime();
            int cpuCores = osBean.getAvailableProcessors();
            long processCpuTimeDiffInMs = TimeUnit.NANOSECONDS
                    .toMillis(newProcessCpuTime - processCpuTime);
            long processUpTimeDiffInMs = newProcessUpTime - processUpTime;
            double processCpuUsage = (double) processCpuTimeDiffInMs / processUpTimeDiffInMs / cpuCores;
            processCpuTime = newProcessCpuTime;
            processUpTime = newProcessUpTime;

            currentCpuUsage = Math.max(processCpuUsage, systemCpuUsage);

            if (currentLoad > AVAILABLE_PROCESSORS || currentCpuUsage > 0.85) {
                writeSystemStatusLog();
            }
        } catch (Throwable e) {
            log.warn("[SystemStatusListener] Failed to get system metrics from JMX", e);
        }
    }

    private void writeSystemStatusLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Load exceeds the threshold: ");
        sb.append("load:").append(String.format("%.4f", currentLoad)).append("; ");
        sb.append("cpuUsage:").append(String.format("%.4f", currentCpuUsage)).append("; ");
        log.error(sb.toString());
    }
}
