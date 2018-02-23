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

public interface Installable {

    default void create(CloudClient client, CloudApp app, int memory, int disc) {
        String buildpackUrl = Buildpacks.getBuildpackUrl(app.getLanguage());
        ApplicationUploader.checkLanguage(buildpackUrl);

        client.createApp(app.getName(), new Staging(null, buildpackUrl), disc, memory,
                Collections.singletonList("https://cf-" + app.getName().toLowerCase() + ".cfapps.io"));
    }

    default void upload(CloudClient client, HttpsURLConnection con, CloudApp app) throws IOException {
        try (InputStream in = con.getInputStream()) {

            client.uploadApp(app.getName(), app.getFileName(), in);

            client.updateAppEnv(app.getName(), ImmutableMap.of("pkgVersion", app.getVersion()));
        }
    }

    void install(CloudApp app, int memory, int disc) throws IOException, ParseException, ClassNotFoundException;

    void checkDependencies(CloudApp app);

}
