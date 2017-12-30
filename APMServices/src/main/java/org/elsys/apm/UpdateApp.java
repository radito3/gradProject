package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.HttpStatus;

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
        client = new CloudClient(orgName, spaceName, token);
        client.login();

        try {
            CloudApplication app = client.getApp(appName);
            JSONObject descr = DescriptorWork.getDescriptor(DescriptorWork.DESCRIPTOR_URL);

            JSONObject appJson = (JSONObject) descr.get(appName);
            if (appJson == null) {
                throw new IllegalArgumentException("App " + appName + " no longer supported");
            }

            List<List<Integer>> versions = getVersions(app, appJson);

            if (checkVer(versions.get(0), versions.get(1), 0)) {
                JSONValue file = (JSONValue) appJson.get("file");
                StringBuilder downloadUrl = new StringBuilder(DescriptorWork.DESCRIPTOR_URL);

                String fileName = String.valueOf(file);
                downloadUrl.replace(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length(), fileName);

                uploadApp(appJson, downloadUrl.toString(), appName, fileName);
            } else {
                return Response.status(200).entity("App up-to-date").build();
            }
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.status(404).entity("App " + appName + " not found").build();
            } else {
                return Response.status(e.getStatusCode().value()).entity(e.getMessage()).build();
            }
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

    private void uploadApp(JSONObject appJson, String... args) {
        try {
            URL url = new URL(args[0]);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            pushApps(con, args[1], args[2], String.valueOf(appJson.get("pkgVersion")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushApps(HttpsURLConnection con, String... args) throws IOException {
        try (InputStream in = con.getInputStream()) {
            String appName = args[0];
            String fileName = args[1];
            String execFileName = fileName.split("[^-a-zA-Z0-9]")[0];

            String nameToUpload = appName.toLowerCase().equals(execFileName.toLowerCase()) ? appName : execFileName;

            client.uploadApp(nameToUpload, fileName, in, UploadStatusCallback.NONE);

            client.updateAppEnv(nameToUpload, ImmutableMap.of("pkgVersion", args[2])); //should test if this erases the repoAppName
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
