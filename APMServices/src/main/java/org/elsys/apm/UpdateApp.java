package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONObject;

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

            JSONObject appJson = (JSONObject) descr.get(appName);
            if (appJson == null) {
                throw new IllegalArgumentException("App " + appName + " no longer supported");
            }

            List<List<Integer>> versions = getVersions(app, appJson);

            if (checkVer(versions.get(0), versions.get(1), 0)) {
                String file = String.valueOf(appJson.get("file"));
                StringBuilder downloadUrl = new StringBuilder(Descriptor.DESCRIPTOR_URL);

                downloadUrl.replace(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length(), file);

                uploadApp(appJson, downloadUrl.toString(), appName, file);
            } else {
                return Response.status(200).entity("App up-to-date").build();
            }

        } catch (CloudFoundryException e) {
            return Response.status(404).entity("App " + appName + " not found").build();
        } catch (IllegalArgumentException e) {
            return Response.status(410).entity(e.getMessage()).build();
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

    private void uploadApp(JSONObject appJson, String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            pushApps(con, appName, fileName, String.valueOf(appJson.get("pkgVersion")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushApps(HttpsURLConnection con, String appName, String fileName, String version) throws IOException {
        try (InputStream in = con.getInputStream()) {

            client.uploadApp(appName, fileName, in);

            client.updateAppEnv(appName, ImmutableMap.of("pkgVersion", version));
        }
    }

    private List<List<Integer>> getVersions(CloudApplication app, JSONObject appJson) {
        String repoVer = String.valueOf(appJson.get("pkgVersion"));
        String currentVer = app.getEnvAsMap().get("pkgVersion");

        List<Integer> repoVersions = new ArrayList<>();
        List<Integer> currentVersions = new ArrayList<>();

        Arrays.stream(currentVer.split("\\.")).forEach(ver -> currentVersions.add(Integer.parseInt(ver)));
        Arrays.stream(repoVer.split("\\.")).forEach(ver -> repoVersions.add(Integer.parseInt(ver)));

        return Arrays.asList(currentVersions, repoVersions);
    }
}
