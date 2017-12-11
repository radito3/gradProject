package org.elsys.apm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;
import org.glassfish.jersey.process.Inflector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClientProvider client;

    private static final String buildpackUrl = Buildpacks.JAVA.getUrl();

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudControllerClientProvider(orgName, spaceName, token);
        client.login();

        StringBuilder staticAppUrl = new StringBuilder(CloudControllerClientProvider.getStaticAppUrl());
        try {
            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(staticAppUrl.append("/descriptor.json").toString());
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            JSONArray files = (JSONArray) app.get("files");
            for (Object file : files) {
                String fileName = String.valueOf(file);
                staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), fileName);
                installApp(staticAppUrl.toString(), appName, fileName);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

    private void installApp(String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();

            // CF supports names containing unusual symbols e.g. '-','/',' ' etc. It may be a pain in the b to handle such though
            String nameToUpload = Objects.equals(appName.toLowerCase(), fileName.split("-")[0].toLowerCase()) ?
                    appName : fileName.split("-")[0];

            String name = Inflector.getInstance().lowerCamelCase(nameToUpload);

            // You should be abble to create the app and later query the CF cloud controller to see what hostname it has assigned
            client.createApp(name, new Staging(null, buildpackUrl), 1000, 1000,
                    Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"));

            client.uploadApp(name, fileName, in, UploadStatusCallback.NONE);

            CloudApplication app = client.getApp(name);
            HashMap<Object, Object> ver = new HashMap<>();
            // why put this hard-coded version?
            ver.put("appVersion", "1.0.0");
            // this only updates this object (pojo) inside this jvm. No network calls are made to the platform's CC
            app.setEnv(ver);
            // close streams in finally - see other comments
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 }
