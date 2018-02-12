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

public final class Descriptor {

    private static volatile Descriptor instance;

    private JSONObject descriptor;

    private Descriptor() throws IOException, ParseException {
        URL url = new RepositoryURLBuilder().repoRoot().repoDescriptor().build();
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        descriptor = getJson(con);
    }

    public static Descriptor getDescriptor() throws IOException, ParseException {
        if (instance == null) {
            synchronized (Descriptor.class) {
                if (instance == null) {
                    instance = new Descriptor();
                }
            }
        }
        return instance;
    }

    public void checkForApp(String appName) throws ClassNotFoundException {
        if (descriptor.get(appName) == null) {
            throw new ClassNotFoundException("App " + appName + " not found");
        }
    }

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
            return buildJson(in);
        }
    }

    private JSONObject buildJson(InputStream inputStream) throws IOException, ParseException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

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
