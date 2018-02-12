package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CloudClientFactoryTest {

    @Test
    public void newClientTest() {
        CloudClientFactory factory = new CloudClientFactory("test", "test");
        try {
            assertNotNull(factory.newCloudClient("test 2"));
            assertNotNull(factory.newCloudClient("1", "2"));
        } catch (CloudFoundryException e) {
            e.getDescription();
        }
    }
}
