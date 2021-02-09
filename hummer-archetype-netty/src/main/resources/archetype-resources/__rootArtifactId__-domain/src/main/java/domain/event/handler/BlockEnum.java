package ${package}.domain.event.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import ${package}.domain.exception.BlacklistException;
import ${package}.domain.exception.SysBlockException;
import ${package}.domain.exception.WhitelistException;

public enum BlockEnum {
    BLOCK_API,
    BLOCK_USER,
    BLOCK_SYSTEM;

    public static BlockEnum getBlockType(BlockException e) {
        if (e instanceof WhitelistException || e instanceof BlacklistException) {
            return BlockEnum.BLOCK_USER;
        }
        if (e instanceof SysBlockException) {
            return  BLOCK_SYSTEM;
        }
        return BLOCK_API;
    }

    public static BlockEnum getBlockById(int id) {
        for (BlockEnum blockEnum : BlockEnum.values()) {
            if (blockEnum.ordinal() == id) {
                return blockEnum;
            }
        }
        return BLOCK_API;
    }
}
