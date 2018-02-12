package org.elsys.apm.rest;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.dependency.DependencyHandler;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    private CloudClient client;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUpdateResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudClientFactory(orgName, spaceName).newCloudClient(token);
        client.login();

        try {
            CloudApplication app = client.getApp(appName);
            Descriptor descr = Descriptor.getDescriptor();

            descr.checkForApp(appName);
            CloudApp app1 = descr.getApp(appName);

            List<List<Integer>> versions = getVersions(app, app1);

            if (checkVer(versions.get(0), versions.get(1), 0)) {

                RepositoryURLBuilder fileUrl = new RepositoryURLBuilder();

                uploadApp(fileUrl.repoRoot().target(app1.getFileName()).build(), app1);

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

    private void uploadApp(URL url, CloudApp app) throws IOException, MissingResourceException {
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        DependencyHandler.checkDependencies(app, client);

        pushApps(con, app);
    }

    private void pushApps(HttpsURLConnection con, CloudApp app) throws IOException {
        try (InputStream in = con.getInputStream()) {

            client.uploadApp(app.getName(), app.getFileName(), in);

            client.updateAppEnv(app.getName(), ImmutableMap.of("pkgVersion", app.getVersion()));
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
