package org.elsys.apm;

public class CloudClientFactory {

    private String org;
    private String space;

    public CloudClientFactory(String org, String space) {
        this.org = org;
        this.space = space;
    }

    public CloudClient newCloudClient(String token) {
        return new CloudClientImpl(org, space, token);
    }

    public CloudClient newCloudClient(String user, String pass) {
        return new CloudClientImpl(org, space, user, pass);
    }
}
