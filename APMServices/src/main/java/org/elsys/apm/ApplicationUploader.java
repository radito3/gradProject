package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.util.MissingResourceException;

public class ApplicationUploader implements Installable {

    private CloudClient client;

    private static final int DEFAULT_DISC = 1000;

    private static final int DEFAULT_MEMORY = 1000;

    public ApplicationUploader(CloudClient client) {
        this.client = client;
    }

    public static void checkLanguage(String buildpackUrl) {
        if (buildpackUrl.equals("Unsupported language")) {
            throw new IllegalArgumentException("Unsupported language");
        }
    }

    @Override
    public void install(CloudApp app, int memory, int disc)
            throws IOException, ParseException, ClassNotFoundException {

        if (client.checkForExistingApp(app.getName())) {
            throw new ExistingAppException("App already exists");
        }

        create(client, app, memory, disc);
        upload(client, (HttpsURLConnection) app.getFileUrl().openConnection(), app);

        if (!app.hasDependencies()) return;

        for (Object obj : app.getDependencies()) {

            if (client.checkForExistingApp(obj.toString())) continue;

            Descriptor descr = Descriptor.getDescriptor();
            CloudApp dependency = descr.getApp(obj.toString());

            install(dependency, DEFAULT_MEMORY, DEFAULT_DISC);
        }
    }

    @Override
    public void checkDependencies(CloudApp app) {
        app.getDependencies().forEach(d -> {
            try {
                client.getApp(d.toString());
            } catch (CloudFoundryException e) {
                throw new MissingResourceException("Missing dependencies", app.getName(), d.toString());
            }
        });
    }

    public class ExistingAppException extends RuntimeException {

        ExistingAppException(String message) {
            super(message);
        }
    }
}
