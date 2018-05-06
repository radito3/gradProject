package org.elsys.apm.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class for handling REST calls for deleting applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/delete")
public class DeleteApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    /**
     * Deletes an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be deleted
     * @param request The Json containing the authentication information
     * @return The response of the operation
     * @throws ParseException If the request is an invalid Json
     */
    @DELETE
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDeleteResult(@HeaderParam("auth-type") String authType,
                                    @PathParam("appName") String appName,
                                    String request) throws ParseException {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        CloudClient client;
        JSONObject json = (JSONObject) new JSONParser().parse(request);

        if (authType.equals("token")) {
            client = factory.newCloudClient(json.get("token").toString());
        } else {
            client = factory.newCloudClient(json.get("user").toString(), json.get("pass").toString());
        }

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
