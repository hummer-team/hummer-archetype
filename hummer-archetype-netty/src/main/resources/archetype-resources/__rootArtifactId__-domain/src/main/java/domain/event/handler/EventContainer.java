package ${package}.domain.event.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventContainer {
    private static final Map<BlockEnum, BlockEventHandler> HASH_MAP = new ConcurrentHashMap<>();

    private EventContainer() {

    }

    public static BlockEventHandler getHandler(BlockEnum blockEnum) {
        return HASH_MAP.get(blockEnum);
    }

    public static void registerHandler() {
        HASH_MAP.put(BlockEnum.BLOCK_USER, new UserBlacklistBlockEventHandler());
        HASH_MAP.put(BlockEnum.BLOCK_API, new ApiBlacklistBlockEventHandler());
        HASH_MAP.put(BlockEnum.BLOCK_SYSTEM, new SysBlockEventHandler());
    }
}
