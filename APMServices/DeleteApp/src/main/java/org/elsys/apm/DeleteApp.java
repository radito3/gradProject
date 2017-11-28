package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;

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

            List<CloudApplication> apps = client.getApplications();
            CloudApplication check = null;
            for (CloudApplication app : apps) {
                if (app.getName().equals(appName)) {
                    check = app;
                }
            }
            if (check == null) {
                throw new ClassNotFoundException("App " + appName + " does not exist.");
            } else {
                client.deleteApplication(appName);
                client.logout();
                result.append("App deleted.");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            result.append(e.getMessage());
        }

        return result.toString();
    }

}
