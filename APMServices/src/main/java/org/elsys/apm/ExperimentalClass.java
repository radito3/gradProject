package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class combines the Install, Update and Delete functionality
 */
@Path("/{org}/{space}")
public class ExperimentalClass {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClient client;

    private static final String buildpackUrl = Buildpacks.JAVA.getUrl();

    private StringBuilder staticAppUrl = new StringBuilder(CloudControllerClientProvider.getStaticAppUrl());

    public ExperimentalClass(@HeaderParam("access-token") String token) {
        client = new CloudControllerClientProvider(orgName, spaceName, token).getClient();
    }

    /**
     * Not tested...
     *
     * @param appName the application name
     * @return response
     */
    @POST
    @Path("/install/{appName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@PathParam("appName") String appName) {
        try {
            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(staticAppUrl.append("/descriptor.json").toString());
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            JSONArray files = (JSONArray) app.get("files");
            Iterator it = files.iterator();
            while (it.hasNext()) {
                String file = String.valueOf(it.next());
                staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1,
                        staticAppUrl.length(),
                        file);
                createApp(appName);
                uploadApp(staticAppUrl.toString(), appName, file);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        return Response.status(201).entity("App created").build();
    }

    /**
     * Tested and working...
     *
     * @param appName the application name
     * @return response
     */
    @DELETE
    @Path("/delete/{appName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDeleteResult(@PathParam("appName") String appName) {
        try {
            client.getApplication(appName);
            client.deleteApplication(appName);
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.status(404).entity("App " + appName + " not found").build();
            } else {
                return Response.status(Integer.parseInt(e.getStatusCode().toString())).entity(e.getMessage()).build();
            }
        }
        return Response.status(200).entity("App deleted").build();
    }

    /**
     * This is inefficiently written.
     * Need to improve!
     *
     * @param appName the application name
     * @return response
     */
    @PUT
    @Path("/update/{appName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUpdateResult(@PathParam("appName") String appName) {
        try {
            CloudApplication app = client.getApplication(appName);

            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(CloudControllerClientProvider.getStaticAppUrl() + "/descriptor.json");

            String repoVer = String.valueOf(descr.get("version"));
            String currentVer = app.getEnvAsMap().get("version");

            Pattern pattern = Pattern.compile("^(\\d).(\\d).(\\d)$");
            Matcher repoVerMatch = pattern.matcher(repoVer);
            Matcher currentVerMatch = pattern.matcher(currentVer);

            if (repoVerMatch.matches() && currentVerMatch.matches()) {
                if (Integer.parseInt(currentVerMatch.group(1)) < Integer.parseInt(repoVerMatch.group(1))
                        || Integer.parseInt(currentVerMatch.group(2)) < Integer.parseInt(repoVerMatch.group(2))) {

                    JSONObject appJson = (JSONObject) descr.get(appName);
                    JSONArray files = (JSONArray) appJson.get("files");
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        String file = String.valueOf(it.next());
                        staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1,
                                staticAppUrl.length(),
                                file);
                        uploadApp(staticAppUrl.toString(), appName, file);
                    }
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
        }
        return Response.status(202).entity("App updated").build();
    }

    private void createApp(String appName) {
        client.createApplication(appName,
                new Staging("--no-manifest", buildpackUrl),
                1000,  //disk space
                1000,  //memory
                Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"),
                null);  //service names

        CloudApplication app = client.getApplication(appName);
        HashMap<Object, Object> ver = new HashMap<>();
        ver.put("version", "1.0.0");
        app.setEnv(ver);
    }

    private void uploadApp(String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();

            String nameToUpload = Objects.equals(appName.toLowerCase(), fileName.split("-")[0].toLowerCase()) ?
                    appName : fileName.split("-")[0];

            client.uploadApplication(nameToUpload, fileName, in, UploadStatusCallback.NONE);

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
