package org.elsys.apm.dependency;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.apm.CloudClient;
import org.elsys.apm.model.CloudApp;
import org.elsys.apm.rest.InstallApp;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class DependencyHandlerTest {

    @Mock
    private CloudApp app;

    @Mock
    private InstallApp install;

    @Mock
    private CloudClient client;

    @Mock
    private JSONArray array;

    @Mock
    private CloudApplication application;

    @Test
    public void handlingNoDependenciesTest() throws ParseException, IOException, ClassNotFoundException {
        when(app.hasDependencies()).thenReturn(false);

        DependencyHandler.handle(app, install, 1, 1);
    }

    @Test(expected = MissingResourceException.class)
    public void checkMissingDependenciesTest() {
        doThrow(CloudFoundryException.class).when(client).getApp(anyString());
        when(array.stream()).thenReturn(Stream.of("one", "two", "three"));
        when(app.getDependencies()).thenReturn(array);

        DependencyHandler.checkDependencies(app, client);
    }

    @Test
    public void checkDependenciesTest() {
        doReturn(application).when(client).getApp(anyString());
        when(array.stream()).thenReturn(Stream.of("one", "two", "three"));
        when(app.getDependencies()).thenReturn(array);

        DependencyHandler.checkDependencies(app, client);
    }

}
