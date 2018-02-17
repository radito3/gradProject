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

public class CloudClientImpl implements CloudClient {

    private static final String TARGET = System.getenv("targetUrl");

    private CloudControllerClient client;

    private CloudControllerClientFactory cloudFactory = new CloudControllerClientFactory(null, true);

    CloudClientImpl(String org, String space, String token) {
        CloudCredentials credentials = new CloudCredentials(new DefaultOAuth2AccessToken(token), false);

        client = cloudFactory.newCloudController(getTargetUrl(), credentials, org, space);
    }

    CloudClientImpl(String org, String space, String user, String pass) {
        CloudCredentials credentials = new CloudCredentials(user, pass);

        client =  cloudFactory.newCloudController(getTargetUrl(), credentials, org, space);
    }

    @Override
    public void login() {
        client.login();
    }

    @Override
    public void uploadApp(String appName, String fileName, InputStream inputStream) throws IOException {
        client.uploadApplication(appName, fileName, inputStream, UploadStatusCallback.NONE);
    }

    @Override
    public void createApp(String appName, Staging staging, Integer disk, Integer memory, List<String> uris) {
        client.createApplication(appName, staging, disk, memory, uris, null);
    }

    @Override
    public void logout() {
        client.logout();
    }

    @Override
    public CloudApplication getApp(String appName) throws CloudFoundryException {
        return client.getApplication(appName);
    }

    @Override
    public void deleteApp(String appName) {
        client.deleteApplication(appName);
    }

    @Override
    public void updateAppEnv(String appName, Map<String, String> env) throws CloudFoundryException {
        client.updateApplicationEnv(appName, env);
    }

    @Override
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
