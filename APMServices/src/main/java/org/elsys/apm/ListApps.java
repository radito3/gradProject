package org.elsys.apm;

import org.elsys.apm.descriptor.Descriptor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/list_repo_apps")
public class ListApps {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getListApps() {
        StringBuilder result = new StringBuilder();
        
        //move the following two lines to descriptor
        Descriptor descr = Descriptor.getDescriptor();
        descr.keySet().stream().forEach(key -> result.append(key).append('\n'));

        return Response.status(200).entity(result.toString()).build();
    }
}
