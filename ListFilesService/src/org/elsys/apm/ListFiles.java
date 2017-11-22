package org.elsys.apm;

import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/List")
public class ListFiles {

    @GET
    @Produces("text/plain")
    public String getListFiles() {
        StringBuilder uri = new StringBuilder();
        uri.append("https://");
        StringBuilder json = new StringBuilder();
        StringBuilder result = new StringBuilder();

        try {
            uri.append(System.getenv("uri"));
            uri.append("/descriptor.json");

            URL url = new URL(uri.toString());
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            InputStream in = con.getInputStream();

            // File tempFile = File.createTempFile("descr", "json", null);
            // FileOutputStream fos = new FileOutputStream(tempFile);

            // byte[] buffer = new byte[4096];
            // while(in.read(buffer) > 0) {
            //     fos.write(buffer);
            // }

            // FileInputStream fis = new FileInputStream(tempFile);
            // FileReader fr = new FileReader(tempFile);

            // fos.close();
            // tempFile.deleteOnExit();

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

}
