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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/{org}/{space}")
public class ExperimentalClass {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    @HeaderParam("access-token")
    private String token;

    private CloudControllerClient client = new CloudControllerClientProvider(orgName, spaceName, token).getClient();
    private static final String buildpackUrl = Buildpacks.JAVA.getUrl();

    @POST
    @Path("/install/{appName}")
    public Response getInstallResult(@PathParam("appName") String appName) {
        StringBuilder staticAppUrl = new StringBuilder(CloudControllerClientProvider.getStaticAppUrl());
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
                installApp(staticAppUrl.toString(), appName, file);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        return Response.status(201).entity("App created").build();
    }

    @DELETE
    @Path("/delete/{appName}")
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

    @PUT
    @Path("/update/{appName}")
    public Response getUpdateResult(@PathParam("appName") String appName) {
        try {
            client.getApplication(appName);

            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(CloudControllerClientProvider.getStaticAppUrl() + "/descriptor.json");
            //check for newer version
            String ver = String.valueOf(descr.get("version"));
            Pattern pattern = Pattern.compile("^(\\d).(\\d).(\\d)$");
            Matcher matcher = pattern.matcher(ver);
            int Major = Integer.parseInt(matcher.group(1));
            int Minor = Integer.parseInt(matcher.group(2));
            int Build = Integer.parseInt(matcher.group(3));

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

    private void installApp(String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();

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

            client.uploadApplication(appName, fileName, in, UploadStatusCallback.NONE);

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
