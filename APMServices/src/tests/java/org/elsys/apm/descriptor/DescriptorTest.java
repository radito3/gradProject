package org.elsys.apm.descriptor;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DescriptorTest {

    @Test
    public void constructionTest() throws IOException, ParseException {
        Descriptor descriptor = Descriptor.getDescriptor();
        assertNotNull(descriptor);
        assertTrue(descriptor instanceof Descriptor);
    }

    @Test(expected = ClassNotFoundException.class)
    public void checkNonExistantAppTest() throws IOException, ParseException, ClassNotFoundException {
        Descriptor.getDescriptor().checkForApp("asdf");
    }

    @Test(expected = ClassNotFoundException.class)
    public void getNonExistantAppTest() throws IOException, ParseException, ClassNotFoundException {
        Descriptor.getDescriptor().getApp("asdf");
    }

    @Test
    public void getKeySetTest() throws IOException, ParseException {
        assertNotNull(Descriptor.getDescriptor().keySet());
    }

}
