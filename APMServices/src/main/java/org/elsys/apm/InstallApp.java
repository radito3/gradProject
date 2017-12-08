package org.elsys.apm;

import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.modeshape.common.text.Inflector;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClient client;

    private static final String buildpackUrl = Buildpacks.JAVA.getUrl();

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getInstallResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudControllerClientProvider(orgName, spaceName, token).getClient();
        StringBuilder result = new StringBuilder();
        StringBuilder staticAppUrl = new StringBuilder(CloudControllerClientProvider.getStaticAppUrl());
        try {
            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(staticAppUrl.append("/descriptor.json").toString());
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            JSONArray files = (JSONArray) app.get("files");
            for (Object file1 : files) {
                String file = String.valueOf(file1);
                staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1,
                        staticAppUrl.length(),
                        file);
                result.append(installApp(staticAppUrl.toString(), appName, file));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            result.append(e.getMessage());
        }
        client.logout();
        return result.toString();
    }

    private String installApp(String uri, String appName, String fileName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();

            String nameToUpload = Objects.equals(appName.toLowerCase(), fileName.split("-")[0].toLowerCase()) ?
                    appName : fileName.split("-")[0];

            String name = Inflector.getInstance().lowerCamelCase(nameToUpload);

            client.createApplication(name,
                    new Staging("--no-manifest", buildpackUrl),
                    1000,  //disk space
                    1000,  //memory
                    Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"),
                    null);  //service names

            client.uploadApplication(name, fileName, in, UploadStatusCallback.NONE);

            CloudApplication app = client.getApplication(name);
            HashMap<Object, Object> ver = new HashMap<>();
            ver.put("version", "1.0.0");
            app.setEnv(ver);

            in.close();
        } catch (IOException e) {
            return "Error installing app";
        }

        return "App installed successfully\n";
    }
 }
