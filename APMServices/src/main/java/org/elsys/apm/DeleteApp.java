package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.springframework.http.HttpStatus;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.elsys.apm.CloudControllerClientProvider.getInstance;

@Path("/{org}/{space}/delete/{appName}")
public class DeleteApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClient client = getInstance(orgName, spaceName).getClient();

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String getDeleteResult(@PathParam("appName") String appName) {
        StringBuilder result = new StringBuilder();
        try {
            client.getApplication(appName);
            client.deleteApplication(appName);
            result.append("App deleted");
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                result.append(String.format("App %s not found", appName));
            } else {
                result.append(e.getMessage());
            }
        }
        client.logout();
        return result.toString();
    }
}
