package org.elsys.apm;

import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONObject;

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

        JSONObject descr = Descriptor.getDescriptor();
        descr.keySet().stream().forEach(key -> result.append(key).append('\n'));

        return Response.status(200).entity(result.toString()).build();
    }
}
