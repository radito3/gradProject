package org.elsys.apm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CloudClientFactoryTest {

    @Mock
    private CloudClient clientMock;

    @Mock
    private CloudClientFactory factoryMock;

    @Test
    public void newClientTokenTest() {
        when(factoryMock.newCloudClient(anyString())).thenReturn(clientMock);

        assertNotNull(factoryMock.newCloudClient(anyString()));
        assertEquals(factoryMock.newCloudClient(anyString()), clientMock);
    }

    @Test
    public void newClientPassTest() {
        when(factoryMock.newCloudClient(anyString(), anyString())).thenReturn(clientMock);

        assertNotNull(factoryMock.newCloudClient(anyString(), anyString()));
        assertEquals(factoryMock.newCloudClient(anyString(), anyString()), clientMock);
    }
}
