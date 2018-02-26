package org.elsys.apm.rest;

import org.elsys.apm.ApplicationUploader;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@HeaderParam("access-token") String token,
                                     @PathParam("appName") String appName,
                                     @DefaultValue("1000") @QueryParam("mem") int memory,
                                     @DefaultValue("1000") @QueryParam("disc") int disc) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        CloudClient client = factory.newCloudClient(token);

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
