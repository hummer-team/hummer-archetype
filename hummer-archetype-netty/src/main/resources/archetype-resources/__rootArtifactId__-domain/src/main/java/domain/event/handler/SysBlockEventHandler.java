package ${package}.domain.event.handler;

import ${package}.domain.event.BlockActionEvent;
import ${package}.domain.event.BlockEvent;
import ${package}.support.cache.CacheWrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static ${package}.domain.Constant.DEFAULT_MAX_LOCK_TARGET_MAP_SIZE;
import static ${package}.domain.Constant.LOCK_SYS_TARGET;

public class SysBlockEventHandler implements BlockEventHandler {
    @Override
    public void handler(BlockEvent event, BlockEnum blockEnum) {
        BlockActionEvent event1 = new BlockActionEvent();
        event1.setAtExpired(Date.from(LocalDateTime.now().plusSeconds(event.getTimeWindow())
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        event1.putLockTarget(event.getBlockTarget()
                ,event.getTimeWindow()
                ,DEFAULT_MAX_LOCK_TARGET_MAP_SIZE);
        event1.setCode(event.getCode());
        CacheWrapper.<String, BlockActionEvent>localCache().put(LOCK_SYS_TARGET, event1);
    }
}
