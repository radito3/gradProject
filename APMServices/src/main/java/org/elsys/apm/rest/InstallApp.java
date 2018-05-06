package org.elsys.apm.rest;

import org.elsys.apm.ApplicationUploader;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Class for handling the REST calls for installing applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/install")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    /**
     * Installs an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be installed
     * @param memory The memory with which to install
     * @param disc The disc space with which to install
     * @param request The Json containing the authentication information
     * @return The response of the operation
     * @throws ParseException If the request is an invalid Json
     */
    @POST
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@HeaderParam("auth-type") String authType,
                                     @PathParam("appName") String appName,
                                     @DefaultValue("1000") @QueryParam("mem") int memory,
                                     @DefaultValue("1000") @QueryParam("disc") int disc,
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
            Descriptor descr = Descriptor.getDescriptor();
            descr.checkForApp(appName);

            if (client.checkForExistingApp(appName)) {
                return Response.status(400).entity("App already exists").build();
            }

            CloudApp app = descr.getApp(appName);

            ApplicationUploader uploader = new ApplicationUploader(client);

            uploader.install(app, memory, disc);

        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();

        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(e.getMessage()).build();

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();

        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

 }
