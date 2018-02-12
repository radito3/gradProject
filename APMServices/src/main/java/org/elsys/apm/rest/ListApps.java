package org.elsys.apm.rest;

import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/{org}/{space}/list_apps")
public class ListApps {

    @GET
    @Path("/repo")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRepoApps() {
        StringBuilder result = new StringBuilder();

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.keySet().forEach(key -> result.append(key).append('\n'));

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(200).entity(result.toString()).build();
    }

    @GET
    @Path("/installed")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstalledApps(@HeaderParam("access-token") String token,
                                     @PathParam("org") String org, @PathParam("space") String space) {
        StringBuilder result = new StringBuilder();

        CloudClient client = new CloudClientFactory(org, space).newCloudClient(token);

        client.getApps().forEach(app -> result.append(app.getName()).append('\n'));

        return Response.status(200).entity(result.toString()).build();
    }
}
