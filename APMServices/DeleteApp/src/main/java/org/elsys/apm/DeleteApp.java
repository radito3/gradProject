package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.*;
import org.springframework.http.HttpStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/{appName}")
public class DeleteApp {

    private static final String user = System.getenv("user");
    private static final String password = System.getenv("pass");
    private static final String target = "https://api.run.pivotal.io";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getDeleteResult(@PathParam("appName") String appName) {
        StringBuilder result = new StringBuilder();

        try {
            CloudFoundryClient client = new CloudFoundryClient(
                    new CloudCredentials(user, password),
                    new URL(target),
                    new CloudSpace(CloudEntity.Meta.defaultMeta(), "development",
                            new CloudOrganization(CloudEntity.Meta.defaultMeta(), "graduationProject.org")));
            client.login();

            CloudApplication app = client.getApplication(appName);
            client.deleteApplication(appName);
            client.logout();
            result.append("App deleted.");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                result.append(String.format("App %s not found", appName));
            } else {
                result.append(e.getMessage());
            }
        }

        return result.toString();
    }

}
