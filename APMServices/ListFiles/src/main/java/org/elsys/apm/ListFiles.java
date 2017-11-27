package org.elsys.apm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/List")
public class ListFiles {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getListFiles() {
        StringBuilder uri = new StringBuilder("https://");
        StringBuilder json = new StringBuilder();
        StringBuilder result = new StringBuilder();

        try {
            uri.append(System.getenv("uri")).append("/descriptor.json");

            URL url = new URL(uri.toString());
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
            Stream<?> keySet = obj.keySet().stream();
            keySet.forEach(key -> {
                result.append(key);
                result.append('\n');
            });

            in.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

}
