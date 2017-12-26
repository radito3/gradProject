package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
                if (checkVer(currentVerMatch, repoVerMatch, 1)) {

                    JSONArray files = (JSONArray) appJson.get("files");
                    StringBuilder downloadUrl = new StringBuilder(DescriptorWork.DESCRIPTOR_URL);
                    for (Object file : files) {
                        String fileName = fileNameToDownload(appJson, String.valueOf(file));
                        downloadUrl.replace(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length(), fileName);

                        uploadApp(downloadUrl.toString(), appName, fileName, appJson);
                    }
                } else {
                    return Response.status(200).entity("App up-to-date").build();
                }
            }
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.status(404).entity("App " + appName + " not found").build();
            } else {
                return Response.status(Integer.parseInt(e.getStatusCode().toString())).entity(e.getMessage()).build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(410).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(202).entity("App updated").build();
    }

    private boolean checkVer(Matcher matcher1, Matcher matcher2, int depth) {
        if (Integer.parseInt(matcher1.group(depth)) < Integer.parseInt(matcher2.group(depth))) {
            return true;
        } else if (depth == 3) {
            return false;
        } else {
            return checkVer(matcher1, matcher2, depth + 1);
        }
    }

    private String fileNameToDownload(JSONObject appJson, String fileName) {
        String repoAppName = String.valueOf(appJson.get("repoAppName"));
        if (repoAppName.equals("null")) {
            return fileName;
        } else {
            return repoAppName;
        }
    }

    private void uploadApp(String uri, String appName, String fileName, JSONObject appJson) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            String appVer = String.valueOf(appJson.get("appVersion"));

            pushApps(con, appName, fileName, appVer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushApps(HttpsURLConnection con, String... args) throws IOException {
        try (InputStream in = con.getInputStream()) {
            String appName = args[0];
            String fileName = args[1];
            String execFileName = fileName.split("[^a-zA-Z0-9]")[0];

            String nameToUpload = appName.toLowerCase().equals(execFileName.toLowerCase()) ? appName : execFileName;

            client.uploadApp(nameToUpload, fileName, in, UploadStatusCallback.NONE);

            client.updateAppEnv(nameToUpload, ImmutableMap.of("appVersion", args[2])); //should test if this erases the repoAppName
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
