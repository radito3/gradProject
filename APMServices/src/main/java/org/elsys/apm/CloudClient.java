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

    private CloudControllerClientFactory cloudFactory = new CloudControllerClientFactory(null, true);

    CloudClient(String org, String space, String token) {
        String tokenStr = token.split(" ")[1]; //input token is 'bearer <token_string>'

        CloudCredentials credentials = new CloudCredentials(new DefaultOAuth2AccessToken(tokenStr), false);

        client = cloudFactory.newCloudController(getTargetUrl(), credentials, org, space);
    }

    CloudClient(String org, String space, String user, String pass) {
        CloudCredentials credentials = new CloudCredentials(user, pass);

        client =  cloudFactory.newCloudController(getTargetUrl(), credentials, org, space);
    }

    public void login() {
        client.login();
    }

    public void uploadApp(String appName, String fileName, InputStream inputStream) throws IOException {
        client.uploadApplication(appName, fileName, inputStream, UploadStatusCallback.NONE);
    }

    public void createApp(String appName, Staging staging, Integer disk, Integer memory, List<String> uris) {
        client.createApplication(appName, staging, disk, memory, uris, null);
    }

    public void logout() {
        client.logout();
    }

    public CloudApplication getApp(String appName) throws CloudFoundryException {
        return client.getApplication(appName);
    }

    public void deleteApp(String appName) {
        client.deleteApplication(appName);
    }

    public void updateAppEnv(String appName, Map<String, String> env) throws CloudFoundryException {
        client.updateApplicationEnv(appName, env);
    }

    public List<CloudApplication> getApps() {
        return client.getApplications();
    }

    private URL getTargetUrl() {
        try {
            return new URL(TARGET);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Target Url", e);
        }
    }
}
