package org.elsys.apm;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/list")
public class ListApps {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getListApps() {
        StringBuilder result = new StringBuilder();
        try {
            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(CloudControllerClientProvider.getStaticAppUrl() + "/descriptor.json");
            descr.keySet().stream().forEach(key -> result.append(key).append('\n'));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
