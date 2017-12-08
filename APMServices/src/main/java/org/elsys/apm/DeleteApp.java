package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.springframework.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/{org}/{space}/delete/{appName}")
public class DeleteApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String getDeleteResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        CloudControllerClient client = new CloudControllerClientProvider(orgName, spaceName, token).getClient();
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
