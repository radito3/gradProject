package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

@Path("/{appName}")
public class InstallApp {

    private static final String user = System.getenv("user");
    private static final String password = System.getenv("pass");
    private static final String target = "https://api.run.pivotal.io";
    private static final String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getInstallResult(@PathParam("appName") String appName) {
        StringBuilder staticAppUri = new StringBuilder("https://");
        staticAppUri.append(System.getenv("staticAppUri"));
        StringBuilder result = new StringBuilder();

        try {
            JSONObject json = getDescriptor(staticAppUri.append("/descriptor.json").toString());
            JSONObject app;
            if ((app = (JSONObject) json.get(appName)) == null) {
                throw new ClassNotFoundException("App " + appName + " does not exist.");
            }

            JSONArray files = (JSONArray) app.get("files");
            files.stream().forEach(file -> {
                staticAppUri.replace(staticAppUri.lastIndexOf("/") + 1,
                        staticAppUri.length(),
                        String.valueOf(file));
                result.append(installApp(staticAppUri.toString(), appName));
            });

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            result.append(e.getMessage());
        }

        return result.toString();
    }

    private JSONObject getDescriptor(String uri) throws IOException, ParseException {
        StringBuilder json = new StringBuilder();
        URL url = new URL(uri);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        InputStream in = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = br.readLine()) != null) {
            json.append(line).append('\n');
        }

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json.toString());

        in.close();
        return obj;
    }

    private String installApp(String uri, String appName) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            InputStream in = con.getInputStream();

            CloudFoundryClient client = new CloudFoundryClient(
                    new CloudCredentials(user, password),
                    new URL(target),
                    new CloudSpace(CloudEntity.Meta.defaultMeta(), "development",
                            new CloudOrganization(CloudEntity.Meta.defaultMeta(), "graduationProject")));
            client.login();

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
            
            client.uploadApplication(appName, uri.substring(uri.lastIndexOf("/")), in);

            client.logout();
            in.close();
        } catch (IOException e) {
            return "Error installing app.";
        }

        return "App installed successfully.";
    }

}
