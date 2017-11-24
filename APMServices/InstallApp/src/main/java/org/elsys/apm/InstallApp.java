package org.elsys.apm;

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
        StringBuilder staticAppUri = new StringBuilder();
        StringBuilder json = new StringBuilder();
        StringBuilder result = new StringBuilder();
        staticAppUri.append("https://");
        staticAppUri.append(System.getenv("staticAppUri"));
        staticAppUri.append("/descriptor.json");

        try {
            URL url = new URL(staticAppUri.toString());
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
            obj.get(appName);

            in.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
