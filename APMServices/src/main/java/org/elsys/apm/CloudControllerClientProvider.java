package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

final class CloudControllerClientProvider {

    private static final String staticAppUrl = System.getenv("staticAppUrl");
//    private static final String user = System.getenv("user");
//    private static final String pass = System.getenv("pass");
    private static final String target = "https://api.run.pivotal.io";
    private CloudControllerClient client;

    CloudControllerClientProvider(String org, String space, String token) {
        CloudControllerClientFactory cl = new CloudControllerClientFactory(null, true);
        client = cl.newCloudController(getTargetUrl(),
                new CloudCredentials(new DefaultOAuth2AccessToken(token)), org, space);
        client.login();
    }

    static String getStaticAppUrl() {
        return staticAppUrl;
    }

    CloudControllerClient getClient() {
        return client;
    }

    static JSONObject getDescriptor(String uri) throws IOException, ParseException {
        StringBuilder json = new StringBuilder();
        URL url = new URL(uri);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        InputStream in = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = br.readLine()) != null) {
            json.append(line).append('\n');
        }

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json.toString());

        in.close();
        br.close();
        return obj;
    }

    private URL getTargetUrl() {
        try {
            return new URL(target);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
