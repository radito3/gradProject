package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.util.MissingResourceException;

/**
 * Class for uploading applications and checking dependencies.
 * Implements the {@link Installable} interface.
 *
 * @author Rangel Ivanov
 */
public class ApplicationUploader implements Installable {

    private CloudClient client;

    private static final int DEFAULT_DISC = 1000;

    private static final int DEFAULT_MEMORY = 1000;

    public ApplicationUploader(CloudClient client) {
        this.client = client;
    }

    /**
     * Check for a valid buildpack
     *
     * @param buildpackUrl The buildpack url
     */
    public static void checkLanguage(String buildpackUrl) {
        if (buildpackUrl.equals("Unsupported language")) {
            throw new IllegalArgumentException("Unsupported language");
        }
    }

    /**
     * Install a given application
     * Recursively installs its dependencies starting from the lowest level
     *
     * @param app The CloudApp object representing the application to be installed
     * @param memory The amount of operating memory with which to be installed
     * @param disc The amount of disc space with which to be installed
     * @throws IOException If there is an error with building the descriptor json
     * @throws ParseException If there is an error with building the descriptor json
     * @throws ClassNotFoundException If the application does not exist
     */
    @Override
    public void install(CloudApp app, int memory, int disc)
            throws IOException, ParseException, ClassNotFoundException {

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

    /**
     * Check if all dependencies of a given app are present
     *
     * @param app A CloudApp object representing the application
     * @throws MissingResourceException If there are missing dependencies
     */
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
}
