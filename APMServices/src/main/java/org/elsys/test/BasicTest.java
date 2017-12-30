package org.elsys.test;

import org.junit.Test;
import org.junit.Assert;

public class BasicTest {

    @Test
    public void basicTest() {
        Integer one = 1;
        Integer two = 2;
        Assert.assertTrue(one < two);
    }
}
