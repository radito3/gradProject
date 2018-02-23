package org.elsys.apm.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.apm.ApplicationUploader;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

@Path("/{org}/{space}/update/{appName}")
public class UpdateApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUpdateResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        CloudClient client = factory.newCloudClient(token);

        client.login();

        try {
            CloudApplication cloudApp = client.getApp(appName);
            Descriptor descr = Descriptor.getDescriptor();
            descr.checkForApp(appName);

            CloudApp app = descr.getApp(appName);
            List<List<Integer>> versions = getVersions(cloudApp, app);

            if (checkVer(versions.get(0), versions.get(1), 0)) {

                ApplicationUploader uploader = new ApplicationUploader(client);

                uploader.checkDependencies(app);

                uploader.upload(client, (HttpsURLConnection) app.getFileUrl().openConnection(), app);

            } else {
                return Response.status(200).entity("App up-to-date").build();
            }

        } catch (CloudFoundryException e) {
            return Response.status(404).entity("App " + appName + " not found").build();

        } catch (ClassNotFoundException e) {
            return Response.status(410).entity("App " + appName + " no longer supported").build();

        } catch (MissingResourceException e) {
            return Response.status(424).entity(e.getMessage()).build();

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();

        } finally {
            client.logout();
        }

        return Response.status(202).entity("App updated").build();
    }

    private boolean checkVer(List<Integer> currentVers, List<Integer> repoVers, int depth) {
        if (currentVers.get(depth) < repoVers.get(depth)) {
            return true;
        } else if (depth == 2) {
            return false;
        } else {
            return checkVer(currentVers, repoVers, depth + 1);
        }
    }

    private List<List<Integer>> getVersions(CloudApplication cloudApp, CloudApp app) {
        String repoVer = app.getVersion();
        String currentVer = cloudApp.getEnvAsMap().get("pkgVersion");

        List<Integer> repoVersions = new ArrayList<>();
        List<Integer> currentVersions = new ArrayList<>();

        Arrays.stream(currentVer.split("\\.")).forEach(ver -> currentVersions.add(Integer.parseInt(ver)));
        Arrays.stream(repoVer.split("\\.")).forEach(ver -> repoVersions.add(Integer.parseInt(ver)));

        return Arrays.asList(currentVersions, repoVersions);
    }
}
