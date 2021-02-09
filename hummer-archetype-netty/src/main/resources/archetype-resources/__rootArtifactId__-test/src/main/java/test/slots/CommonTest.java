package ${package}.test.slots;

import org.junit.Test;

public class CommonTest {
    @Test
    public void doubleToLong(){
        System.out.println(Double.doubleToLongBits(1.0));
        System.out.println(Double.doubleToRawLongBits(1.0));
    }
}
