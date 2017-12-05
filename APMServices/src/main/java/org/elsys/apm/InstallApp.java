package org.elsys.apm;

import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.Staging;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;

import static org.elsys.apm.CloudControllerClientProvider.getInstance;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClient client = getInstance(orgName, spaceName).getClient();
    private static final String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getInstallResult(@PathParam("appName") String appName) {
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
            Iterator it = files.iterator();
            while (it.hasNext()) {
                String file = String.valueOf(it.next());
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

            client.createApplication(appName,
                    new Staging("--no-manifest", buildpackUrl),
                    1000,  //disk space
                    1000,  //memory
                    Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"),
                    null);  //service names

//            CloudApplication app = client.getApplication(appName);
//            HashMap<Object, Object> ver = new HashMap<>();
//            ver.put("version", "1.0.0");
//            app.setEnv(ver);

            client.uploadApplication(appName, fileName, in, UploadStatusCallback.NONE);

            in.close();
        } catch (IOException e) {
            return "Error installing app";
        }

        return "App installed successfully";
    }
}
