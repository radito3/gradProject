package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.*;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientFactory;
import org.springframework.http.HttpStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Path("/{appName}")
public class DeleteApp {

    private static final String user = System.getenv("user");
    private static final String password = System.getenv("pass");
    private static final String target = "https://api.run.pivotal.io";
    private static final String orgName = "graduationProject.org";
    private static final String spaceName = "development";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getDeleteResult(@PathParam("appName") String appName) {
        StringBuilder result = new StringBuilder();

        try {
            CloudControllerClientFactory cl = new CloudControllerClientFactory(null, true);
            CloudControllerClient client = cl.newCloudController(new URL(target), new CloudCredentials(user, password), orgName, spaceName);
            client.getSpaces().forEach(s -> System.err.println(s));
            /*CloudFoundryClient client = new CloudFoundryClient(
                    new CloudCredentials(user, password),
                    new URL(target));,
                    new CloudSpace(CloudEntity.Meta.defaultMeta(), "development",
                            new CloudOrganization(CloudEntity.Meta.defaultMeta(), "graduationProject.org")));*/
            client.login();
            System.err.println("quering applications...");
            List<CloudApplication> apps = client.getApplications();
            apps.forEach(e -> System.err.println(e.getName()));
            CloudApplication app = client.getApplication(appName);
            System.err.print("got app..."+app.toString());

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
