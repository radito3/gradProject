package org.elsys.apm;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class DescriptorWork {

    static String DESCRIPTOR_URL = System.getenv("staticAppUrl").concat("/descriptor.json");

    static JSONObject getDescriptor(String uri) {
        HttpsURLConnection con = null;
        try {
            URL url = new URL(uri);
            con = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fnc(con);
    }

    private static JSONObject fnc(HttpsURLConnection connection) {
        try (InputStream in = connection.getInputStream()) {
            return fnc1(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject fnc1(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder json = new StringBuilder();
            JSONParser parser = new JSONParser();

            String line;
            while ((line = br.readLine()) != null) {
                json.append(line).append('\n');
            }

            return (JSONObject) parser.parse(json.toString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
