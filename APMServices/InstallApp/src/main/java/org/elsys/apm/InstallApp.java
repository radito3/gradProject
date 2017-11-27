package org.elsys.apm;

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

@Path("/{appName}")
public class InstallApp {

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
                throw new ClassNotFoundException("App does not exist.");
            }

            JSONArray files = (JSONArray) app.get("files");
            files.stream().forEach(file -> {
                if (staticAppUri.indexOf(".") > 0) {
                    staticAppUri.replace(staticAppUri.lastIndexOf("/"),
                            staticAppUri.length(),
                            file.toString());
                } else {
                    staticAppUri.append('/').append(file.toString());
                }

                result.append(installApp(staticAppUri.toString()));
            });

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            result.append(e.getMessage());
        }

        return result.append(result.length() == 0 ? "App installed successfully." : "").toString();
    }

    private JSONObject getDescriptor(String uri) throws IOException, ParseException {
        StringBuilder json = new StringBuilder();

        URL url = new URL(uri);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        InputStream in = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = br.readLine()) != null) {
            json.append(line);
            json.append("\n");
        }

        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json.toString());

        in.close();

        return obj;
    }

    private String installApp(String uri) {

        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            InputStream in = con.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error installing app.";
        }

        return "";
    }

}
