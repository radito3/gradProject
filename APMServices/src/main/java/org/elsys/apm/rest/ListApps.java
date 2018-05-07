package org.elsys.apm.rest;

import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Class for handling the REST calls for listing applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/list_apps")
public class ListApps extends AbstractRestHandler {

    /**
     * Get the repository applications
     *
     * @return A Json representing the applications
     */
    @GET
    @Path("/repo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepoApps() {
        StringBuilder result = new StringBuilder("[");

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.keySet().forEach(key -> result.append('\"').append(key).append("\","));

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(errorMessage(e.getMessage())).build();
        }

        String output = result.replace(result.lastIndexOf(","), result.length(), "]").toString();
        return Response.status(200).entity(template.format(new Object[]{"", "", output})).build();
    }

    /**
     * Get the currently installed applications
     *
     * @param authType The authentication type
     * @param org The organisation name
     * @param space The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the applications
     */
    @GET
    @Path("/installed")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstalledApps(@HeaderParam("auth-type") String authType,
                                     @PathParam("org") String org,
                                     @PathParam("space") String space,
                                     String request) {
        CloudClientFactory factory = new CloudClientFactory(org, space);
        createClient(factory, request, authType);
        StringBuilder result = new StringBuilder("[");

        client.getApps().forEach(app -> result.append('\"').append(app.getName()).append("\","));

        String output = result.replace(result.lastIndexOf(","), result.length(), "]").toString();
        return Response.status(200).entity(template.format(new Object[]{"", "", output})).build();
    }
}
