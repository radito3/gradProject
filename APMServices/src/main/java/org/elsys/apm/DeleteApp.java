package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/{org}/{space}/delete/{appName}")
public class DeleteApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDeleteResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        CloudClient client = new CloudClientFactory(orgName, spaceName).newCloudClient(token);
        client.login();

        try {
            client.getApp(appName);
            client.deleteApp(appName);

        } catch (CloudFoundryException e) {
            return Response.status(404).entity("App " + appName + " does not exist").build();

        } finally {
            client.logout();
        }

        return Response.status(200).entity("App deleted").build();
    }
}
