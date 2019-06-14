package org.elsys.apm;

/**
 * A Factory from which the CloudClient objects are constructed
 *
 * @author Rangel Ivanov
 */
public class CloudClientFactory {

    private String org;
    private String space;

    public CloudClientFactory(String org, String space) {
        this.org = org;
        this.space = space;
    }

    /**
     * Build a CloudClient with an OAuth2 token
     *
     * @param token The token
     * @return A new CloudClient instance
     */
    public CloudClient newCloudClient(String token) {
        return new CloudClientImpl(org, space, token);
    }

    /**
     * Build a CloudClient with a Username and Password
     *
     * @param user The username
     * @param pass The password
     * @return A new CloudClient instance
     */
    public CloudClient newCloudClient(String user, String pass) {
        return new CloudClientImpl(org, space, user, pass);
    }
}
