package org.elsys.test;

import org.junit.Test;
import org.junit.Assert;

public class BasicTest {
//This test seems irrelevant to the project.
//A good idea is to create another source folder in the same project, which has the same java package structure as the original code.
//you can place the test classes in the coresponding packages - this way the test code would have access to the package-private memthods/fields of the classes 
    @Test
    public void basicTest() {
        Integer one = 1;
        Integer two = 2;
        Assert.assertTrue(one < two);
    }
}
