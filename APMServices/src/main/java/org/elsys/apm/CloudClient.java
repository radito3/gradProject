package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The Cloud Client interface with which the CloudFoundry API functions are called.
 *
 * @author Rangel Ivanov
 */
public interface CloudClient {

    /**
     * Log in to the CloudFoundry profile using the given credentials
     */
    void login();

    /**
     * Upload an application to CloudFoundry
     *
     * @param appName The application name
     * @param fileName The file name to be uploaded
     * @param inputStream The input stream of data
     * @throws IOException If there is an IO error
     */
    void uploadApp(String appName, String fileName, InputStream inputStream) throws IOException;

    /**
     * Create an application in CloudFoundry
     *
     * @param appName The application name
     * @param staging The Staging object with which to stage the app
     * @param disk The disc space
     * @param memory The operating memory
     * @param uris A List of uris upon which the application may be called
     */
    void createApp(String appName, Staging staging, Integer disk, Integer memory, List<String> uris);

    /**
     * Log out of the CloudFoundry profile
     */
    void logout();

    /**
     * Get an application from a designated org and space
     *
     * @param appName The application name
     * @return The {@link CloudApplication} object representing the application
     * @throws CloudFoundryException If the app does not exist
     */
    CloudApplication getApp(String appName) throws CloudFoundryException;

    /**
     * Check if a given application exists in the designated org and space
     *
     * @param appName The application name to check
     * @return True if it exists, false otherwise
     */
    boolean checkForExistingApp(String appName);

    /**
     * Delete an application in the designated org and space
     *
     * @param appName The application name to be deleted
     */
    void deleteApp(String appName);

    /**
     * Update an application environment
     *
     * @param appName The application name
     * @param env A {@link Map} containing the new key value pairs to be added
     * @throws CloudFoundryException If there is an error with the environment map
     */
    void updateAppEnv(String appName, Map<String, String> env) throws CloudFoundryException;

    /**
     * Get the current installed applications from the designated org and space
     *
     * @return A List with the Cloud Applications
     */
    List<CloudApplication> getApps();
}
