package org.elsys.apm.descriptor;

import org.elsys.apm.model.CloudApp;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

/**
 * Class representing the repository descriptor file.
 * Contains information about repository files.
 * Implements the Singleton design pattern.
 */
public class Descriptor {

    private static Descriptor instance;

    private JSONObject descriptor;

    private Descriptor() throws IOException, ParseException {
        URL url = new RepositoryURLBuilder().repoRoot().repoDescriptor().build();
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        descriptor = getJson(con);
    }

    /**
     * Get the Singleton instance
     *
     * @return The instance
     * @throws IOException If an error occurred during creating the InputStream from the HTTP request
     * @throws ParseException If an error occurred during the building of the JSONObject
     */
    public static Descriptor getDescriptor() throws IOException, ParseException {
        if (instance == null) {
            instance = new Descriptor();
        }
        return instance;
    }

    /**
     * Checks if an application exists in the repository
     *
     * @param appName The name of the application to be checked
     * @throws ClassNotFoundException If the application does not exist
     */
    public void checkForApp(String appName) throws ClassNotFoundException {
        if (descriptor.get(appName) == null) {
            throw new ClassNotFoundException("App " + appName + " not found");
        }
    }

    /**
     * Get an application
     *
     * @param appName The application name
     * @return The {@link CloudApp} object representing the app
     * @throws ClassNotFoundException If the application is not found
     */
    public CloudApp getApp(String appName) throws ClassNotFoundException {
        if (descriptor.get(appName) == null) {
            throw new ClassNotFoundException("Missing package " + appName);
        }
        return new CloudApp((JSONObject) descriptor.get(appName), appName);
    }

    public Set<?> keySet() {
        return descriptor.keySet();
    }

    private JSONObject getJson(HttpsURLConnection connection) throws IOException, ParseException {
        try (InputStream in = connection.getInputStream()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

                StringBuilder json = new StringBuilder();
                JSONParser parser = new JSONParser();

                String line;
                while ((line = br.readLine()) != null) {
                    json.append(line).append('\n');
                }

                return (JSONObject) parser.parse(json.toString());
            }
        }
    }
}
