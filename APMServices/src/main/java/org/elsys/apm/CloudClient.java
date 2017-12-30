package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CloudClient {

    private static final String TARGET = System.getenv("targetUrl");

    private CloudControllerClient client;

    CloudClient(String org, String space, String token) {
        String tokenStr = token.split(" ")[1];

        CloudControllerClientFactory cloudFactory = new CloudControllerClientFactory(null, true);

        CloudCredentials credentials = new CloudCredentials(new DefaultOAuth2AccessToken(tokenStr), false);

        client = cloudFactory.newCloudController(getTargetUrl(), credentials, org, space);
    }

    void login() {
        client.login();
    }

    void uploadApp(String appName, String fileName, InputStream inputStream,
                   UploadStatusCallback callback) throws IOException {
        client.uploadApplication(appName, fileName, inputStream, callback);
    }

    void createApp(String appName, Staging staging, Integer disk, Integer memory,
                   List<String> uris) {
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

    void updateAppEnv(String appName, Map<String, String> env) {
        client.updateApplicationEnv(appName, env);
    }

    private URL getTargetUrl() {
        try {
            return new URL(TARGET);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return URL.class.getResource("");
    }
}
