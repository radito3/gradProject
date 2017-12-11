package org.elsys.apm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

//A provider implies that the class/objects provides something. On the other hand it's more of a 'Wrapper'
//As the wrapper does not do much more with the wrapped object (e.g what would be the case for e.g. the Adapter pattern), 
//consider transforming this to a Factory (pattern), with the consequence of introducing the dependency to 'CloudControllerClientProvider' in the classes using the current  
final class CloudControllerClientProvider {

    // Use upper case letters for 'constants'
    private static final String staticAppUrl = System.getenv("staticAppUrl");
    // this url could also be acquired via an env-variable - for other cloud providers
    private static final String target = "https://api.run.pivotal.io";
    private CloudControllerClient client;

    CloudControllerClientProvider(String org, String space, String token) {
        CloudControllerClientFactory cl = new CloudControllerClientFactory(null, true);

        client = cl.newCloudController(getTargetUrl(), new CloudCredentials(new DefaultOAuth2AccessToken(token.split(" ")[1]), false), org,
            space);
    }

    void login() {
        client.login();
    }

    void uploadApp(String appName, String fileName, InputStream inputStream, UploadStatusCallback callback) throws IOException {
        client.uploadApplication(appName, fileName, inputStream, callback);
    }

    void createApp(String appName, Staging staging, Integer disk, Integer memory, List<String> uris) {
        client.createApplication(appName, staging, disk, memory, uris, null);
    }

    void logout() {
        client.logout();
    }

    CloudApplication getApp(String appName) throws CloudFoundryException {
        return client.getApplication(appName);
    }

    void deleteApp(String appName) {
        client.deleteApplication(appName);
    }

    // not part of this class' responsibilities, see comment for method below.
    static String getStaticAppUrl() {
        return staticAppUrl;
    }

    // This method does not have much to do with the cloud controller - it belongs somewhere else, maybe a new class of it's own.
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

        // Streams should be always closed in a 'finally' block - to make sure that no OS handles will be left opened and the kernel won't
        // 'starve' for such; If using AutoCloseable, the finally block is taken care of by the JVM and can be skipped.
        // discussions on topic: https://stackoverflow.com/questions/7224658/java-try-finally-block-to-close-stream#7224839
        // https://stackoverflow.com/questions/4727424/java-code-style-for-open-stream-try-finally-block
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
