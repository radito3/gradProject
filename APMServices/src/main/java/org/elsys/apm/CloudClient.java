package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface CloudClient {

    void login();

    void uploadApp(String appName, String fileName, InputStream inputStream) throws IOException;

    void createApp(String appName, Staging staging, Integer disk, Integer memory, List<String> uris);

    void logout();

    CloudApplication getApp(String appName) throws CloudFoundryException;

    void deleteApp(String appName);

    void updateAppEnv(String appName, Map<String, String> env) throws CloudFoundryException;

    List<CloudApplication> getApps();
}
