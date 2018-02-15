package org.elsys.apm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class CloudClientFactoryTest {

    @Mock
    private CloudClient clientMock;

    @Mock
    private CloudClientFactory factoryMock;

    @Test
    public void newClientTest() {
        when(factoryMock.newCloudClient(anyString())).thenReturn(clientMock);
        verify(factoryMock).newCloudClient(anyString());
        assertNotNull(factoryMock.newCloudClient(anyString()));

        when(factoryMock.newCloudClient(anyString(), anyString())).thenReturn(clientMock);
        verify(factoryMock).newCloudClient(anyString(), anyString());
        assertNotNull(factoryMock.newCloudClient(anyString(), anyString()));
    }
}
