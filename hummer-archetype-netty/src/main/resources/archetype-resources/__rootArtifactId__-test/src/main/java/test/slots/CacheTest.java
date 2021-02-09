package ${package}.test.slots;

import ${package}.support.cache.CacheWrapper;
import io.elves.core.properties.ElvesProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CacheTest {
    @Before
    public void before() throws IOException {
        ElvesProperties.load("test");
    }

    @Test
    public void add() {
        CacheWrapper.<String, String>localCache().put("123", "dddd");
        String v = CacheWrapper.<String, String>localCache().get("123");
        System.out.println(v);
        Assert.assertEquals("dddd", v);

        CacheWrapper.<Integer, Integer>localCache().put(345, 123);
        Integer v2 = CacheWrapper.<Integer, Integer>localCache().get(345);
        Assert.assertEquals(Integer.valueOf(123), v2);
    }
}
