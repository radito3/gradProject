package org.elsys.apm;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

//list is not so descriptive. Consider fitting in the CF terminology e.g. using the term 'marketplace'
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
