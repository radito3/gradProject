package org.elsys.apm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

@Path("/{org}/{space}/update/{appName}")
public class UpdateApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClientProvider client;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUpdateResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudControllerClientProvider(orgName, spaceName, token);
        client.login();

        StringBuilder staticAppUrl = new StringBuilder(CloudControllerClientProvider.getStaticAppUrl());
        try {
            CloudApplication app = client.getApp(appName);

            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(CloudControllerClientProvider.getStaticAppUrl() + "/descriptor.json");

            String repoVer = String.valueOf(descr.get("appVersion"));
            String currentVer = app.getEnvAsMap().get("appVersion");

            Pattern pattern = Pattern.compile("^(\\d).(\\d).(\\d)$");
            Matcher repoVerMatch = pattern.matcher(repoVer);
            Matcher currentVerMatch = pattern.matcher(currentVer);
            // the following flow controll statements are a bit hard to read & follow ;)

            // hint: try inverting the expression & returning in the if body - this will reduce one level of nesting
            // btw what happens when one does not match?
            if (repoVerMatch.matches() && currentVerMatch.matches()) {
                // same hint applies here
                if (Integer.parseInt(currentVerMatch.group(1)) < Integer.parseInt(repoVerMatch.group(1))
                        || Integer.parseInt(currentVerMatch.group(2)) < Integer.parseInt(repoVerMatch.group(2))) {

                    // you can improve the readability of the code, if you extract consequent lines of code working together in methods,
                    // with appropriate names ;)
                    JSONObject appJson = (JSONObject) descr.get(appName);
                    JSONArray files = (JSONArray) appJson.get("files");

                    for (Object file1 : files) {
                        String file = String.valueOf(file1);
                        // the knowledge of the 'staticApp' structure is scattered in many classes. Consider encapsulating it in a single
                        // class, with a nice interface
                        staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), file);
                        uploadApp(staticAppUrl.toString(), appName, file);
                    }

                } else {
                    // one may have forget to which 'if' this block applies by now ;)
                    return Response.status(200).entity("App up-to-date").build();
                }
            }
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.status(404).entity("App " + appName + " not found").build();
            } else {
                return Response.status(Integer.parseInt(e.getStatusCode().toString())).entity(e.getMessage()).build();
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            client.logout();
        }

        return Response.status(202).entity("App updated").build();
    }

    private void uploadApp(String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            // the install and update classes have a lot of similar(if not identical) lines of code. Consider reusing them e.g common
            // abstract supper class or by composition one like some common utility etc.
            String nameToUpload = Objects.equals(appName.toLowerCase(), fileName.split("-")[0].toLowerCase()) ?
                    appName : fileName.split("-")[0];

            client.uploadApp(nameToUpload, fileName, in, UploadStatusCallback.NONE);
            // close in finally, see other comments
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
