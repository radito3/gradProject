package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.modeshape.common.text.Inflector;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/{org}/{space}/update/{appName}")
public class UpdateApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudClient client;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUpdateResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudClient(orgName, spaceName, token);
        client.login();

        try {
            CloudApplication app = client.getApp(appName);
            JSONObject descr = DescriptorWork.getDescriptor(DescriptorWork.DESCRIPTOR_URL);

            JSONObject appJson = (JSONObject) descr.get(Inflector.getInstance().upperCamelCase(appName));
            if (appJson == null) {
                throw new IllegalArgumentException("App " + appName + " no longer supported");
            }

            Matcher repoVerMatch = getMatchers(app, appJson).get("repoVerMatch");
            Matcher currentVerMatch = getMatchers(app, appJson).get("currentVerMatch");

            if (repoVerMatch.matches() && currentVerMatch.matches()) {
                if (Integer.parseInt(currentVerMatch.group(1)) >= Integer.parseInt(repoVerMatch.group(1))
                        || Integer.parseInt(currentVerMatch.group(2)) >= Integer.parseInt(repoVerMatch.group(2))) {
                    return Response.status(200).entity("App up-to-date").build();
                }

                JSONArray files = (JSONArray) appJson.get("files");
                StringBuilder staticAppUrl = new StringBuilder(DescriptorWork.STATIC_APP_URL);
                for (Object file : files) {
                    String fileName = String.valueOf(file);
                    staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), fileName);
                    uploadApp(staticAppUrl.toString(), appName, fileName);
                }
            }

        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.status(404).entity("App " + appName + " not found").build();
            } else {
                return Response.status(Integer.parseInt(e.getStatusCode().toString())).entity(e.getMessage()).build();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            return Response.status(410).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(202).entity("App updated").build();
    }

    private void uploadApp(String uri, String appName, String fileName) throws IOException {
        InputStream in = null;
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            in = con.getInputStream();

            String nameToUpload = Objects.equals(appName.toLowerCase(), fileName.split("-")[0].toLowerCase()) ?
                    appName : fileName.split("-")[0];

            client.uploadApp(nameToUpload, fileName, in, UploadStatusCallback.NONE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(in).close();
        }
    }

    private Map<String, Matcher> getMatchers(CloudApplication app, JSONObject appJson) {
        String repoVer = String.valueOf(appJson.get("appVersion"));
        String currentVer = app.getEnvAsMap().get("appVersion");

        Pattern pattern = Pattern.compile("^(\\d).(\\d).(\\d)$");
        Matcher repoVerMatch = pattern.matcher(repoVer);
        Matcher currentVerMatch = pattern.matcher(currentVer);

        return ImmutableMap.of("repoVerMatch", repoVerMatch, "currentVerMatch", currentVerMatch);
    }
}
