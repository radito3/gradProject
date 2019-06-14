package org.elsys.apm.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.CloudClientFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class for handling the REST calls for deleting applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/delete")
public class DeleteApp extends AbstractRestHandler {

    /**
     * Deletes an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be deleted
     * @param orgName The organisation name
     * @param spaceName The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the result of the operation
     */
    @DELETE
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeleteResult(@HeaderParam("auth-type") String authType,
                                    @PathParam("appName") String appName,
                                    @PathParam("org") String orgName,
                                    @PathParam("space") String spaceName,
                                    String request) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        createClient(factory, request, authType);

        client.login();

        try {
            client.getApp(appName);
            client.deleteApp(appName);

        } catch (CloudFoundryException e) {
            return Response.status(404).entity(errorMessage("App " + appName + " does not exist")).build();

        } finally {
            client.logout();
        }

        return Response.status(200).entity(successMessage("App deleted")).build();
    }
}
