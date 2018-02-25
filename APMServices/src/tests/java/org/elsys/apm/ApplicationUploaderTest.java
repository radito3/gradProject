package org.elsys.apm;

import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ApplicationUploaderTest {

    @Mock
    private CloudClient client;

    @Mock
    private CloudApp app;

    private ApplicationUploader uploader;

    @Test
    public void correctLanguageTest() {
        ApplicationUploader.checkLanguage("java");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedLanguageTest() {
        ApplicationUploader.checkLanguage("Unsupported language");
    }

    @Test
    public void correctExceptionMessageOnLanguageTest() {
        try {
            ApplicationUploader.checkLanguage("Unsupported language");
            fail("An exception should have been thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Unsupported language", e.getMessage());
        }
    }

    @Test(expected = ApplicationUploader.ExistingAppException.class)
    public void existingAppTest() throws ParseException, IOException, ClassNotFoundException {
        when(app.getName()).thenReturn("name");
        when(client.checkForExistingApp(eq("name"))).thenReturn(true);

        uploader = new ApplicationUploader(client);

        uploader.install(app, 1, 1);
    }

    @Test
    public void correctExceptionMessageOnAppTest() throws ParseException, IOException, ClassNotFoundException {
        when(app.getName()).thenReturn("name");
        when(client.checkForExistingApp(eq("name"))).thenReturn(true);

        uploader = new ApplicationUploader(client);

        try {
            uploader.install(app, 1, 1);
            fail("An exception should have been thrown");
        } catch (ApplicationUploader.ExistingAppException e) {
            assertEquals("App already exists", e.getMessage());
        }
    }

   private void prepareForInstall() throws IOException {
       when(app.getName()).thenReturn("name");
       when(app.getLanguage()).thenReturn("java");
       when(app.hasDependencies()).thenReturn(false);
       when(app.getFileName()).thenReturn("fileName");

//        URL url = getMockedUrl();

       when(app.getFileUrl()).thenReturn(getMockedUrl());

       when(client.checkForExistingApp("name")).thenReturn(false);
       doNothing().when(client).createApp("name", new Staging(), 1, 1, new ArrayList<>());

       doNothing().when(client).uploadApp("name", "fileName",
               new ByteArrayInputStream("<myList></myList>".getBytes("UTF-8")));
   }

   private URL getMockedUrl() throws IOException {
       HttpsURLConnection mockUrlCon = mock(HttpsURLConnection.class);

       ByteArrayInputStream is = new ByteArrayInputStream("<myList></myList>".getBytes("UTF-8"));
       when(mockUrlCon.getInputStream()).thenReturn(is);

       when(mockUrlCon.getLastModified()).thenReturn(10L, 11L);

       URLStreamHandler stubUrlHandler = new URLStreamHandler() {
           @Override
           protected URLConnection openConnection(URL u) throws IOException {
               return mockUrlCon;
           }
       };

       return new URL("foo", "bar", 99, "/foobar", stubUrlHandler);
   }

   @Test
   public void installWithNoDependenciesTest() throws ParseException, IOException, ClassNotFoundException {
       prepareForInstall();

       uploader = new ApplicationUploader(client);

       uploader.install(app, 1, 1);
   }

   @Test
   public void installWithDependenciesTest() {

   }

}
