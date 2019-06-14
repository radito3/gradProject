package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.apm.model.Buildpacks;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * The interface which can create and upload applications to CloudFoundry
 *
 * @author Rangel Ivanov
 */
public interface Installable {

    /**
     * A default implementation of creating a CloudFoundry application
     *
     * @param client The CloudClient instance
     * @param app The CloudApp object representing the app to be created
     * @param memory The operating memory with which to be created
     * @param disc The disc space with which to be created
     */
    default void create(CloudClient client, CloudApp app, int memory, int disc) {
        String buildpackUrl = Buildpacks.getBuildpackUrl(app.getLanguage());
        ApplicationUploader.checkLanguage(buildpackUrl);

        client.createApp(app.getName(), new Staging(null, buildpackUrl), disc, memory,
                Collections.singletonList("https://cf-" + app.getName().toLowerCase() + ".cfapps.io"));
    }

    /**
     * A default implementation of uploading an application to CloudFoundry
     *
     * @param client The CloudClient instance
     * @param con The HTTPS connection object
     * @param app The CloudApp object representing the app to be uploaded
     * @throws IOException If there is an IO error while creating the input stream of data from the HTTPConnection
     */
    default void upload(CloudClient client, HttpsURLConnection con, CloudApp app) throws IOException {
        try (InputStream in = con.getInputStream()) {

            client.uploadApp(app.getName(), app.getFileName(), in);

            client.updateAppEnv(app.getName(), ImmutableMap.of("pkgVersion", app.getVersion()));
        }
    }

    void install(CloudApp app, int memory, int disc) throws IOException, ParseException, ClassNotFoundException;

    void checkDependencies(CloudApp app);

}
