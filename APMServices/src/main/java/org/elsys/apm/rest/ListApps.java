package org.elsys.apm.rest;

import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Handles the REST calls for listing applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/list_apps")
public class ListApps {

    /**
     * Get the repository applications
     *
     * @return A Json representing the applications
     */
    @GET
    @Path("/repo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepoApps() {
        StringBuilder result = new StringBuilder("\"apps\":[");

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.keySet().forEach(key -> result.append('\"').append(key).append("\","));

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(200).entity(
                result.replace(result.lastIndexOf(","), result.length(), "]").toString()).build();
    }

    /**
     * Get the currently installed applications
     *
     * @param authType The authentication type
     * @param org The organisation name
     * @param space The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the applications
     * @throws ParseException If there is an error during the building of the request Json
     */
    @GET
    @Path("/installed")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstalledApps(@HeaderParam("auth-type") String authType,
                                     @PathParam("org") String org, @PathParam("space") String space,
                                     String request) throws ParseException {
        CloudClientFactory factory = new CloudClientFactory(org, space);
        StringBuilder result = new StringBuilder("\"apps\"");
        CloudClient client;
        JSONObject json = (JSONObject) new JSONParser().parse(request);

        if (authType.equals("token")) {
            client = factory.newCloudClient(json.get("token").toString());
        } else {
            client = factory.newCloudClient(json.get("user").toString(), json.get("pass").toString());
        }

        client.getApps().forEach(app -> result.append(app.getName()).append('\n'));

        return Response.status(200).entity(result.toString()).build();
    }
}
