package main.java.org.elsys.api.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class MessagesService {

    protected static List<Message> messages = new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMsg() {
        String output = buildJSONOutput();
        return Response.status(200).entity(output).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response putMsg(@QueryParam("username") String username, @QueryParam("messages") String msg) {
        messages.add(new Message(username, msg));
        String output = buildJSONOutput();
        return Response.status(200).entity(output).build();
    }

    private String buildJSONOutput() {
        StringBuilder result = new StringBuilder("[");
        messages.forEach(msg -> result.append('[').append(msg.getMessage()).append("],"));
        result.replace(result.length() - 1, result.length(), "").append(']');
        return result.toString();
    }

}
