package org.elsys.apm;

import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.parser.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/list_repo_apps")
public class ListApps {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getListApps() {
        StringBuilder result = new StringBuilder();

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.keySet().forEach(key -> result.append(key).append('\n'));

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(200).entity(result.toString()).build();
    }
}
