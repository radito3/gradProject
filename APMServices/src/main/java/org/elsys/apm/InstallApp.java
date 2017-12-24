package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.Staging;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modeshape.common.text.Inflector;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudClient client;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@HeaderParam("access-token") String token, @PathParam("appName") String appName) {
        client = new CloudClient(orgName, spaceName, token);
        client.login();

        StringBuilder staticAppUrl = new StringBuilder(DescriptorWork.DESCRIPTOR_URL);
        try {
            JSONObject descr = DescriptorWork.getDescriptor(staticAppUrl.toString());
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            String appLang = String.valueOf(app.get("language"));
            if (appLang.equals("null")) {
                throw new IllegalStateException("Multi-language support not yet implemented");
            }

            String buildpackUrl = getLangBuildpack(String.valueOf(appLang));
            if (buildpackUrl.equals("Unsupported language")) {
                throw new IllegalArgumentException("Unsupported language");
            }

            JSONArray files = (JSONArray) app.get("files");
            for (Object file : files) {
                String fileName = String.valueOf(file);
                staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), fileName);
                installApp(staticAppUrl.toString(), appName, fileName, buildpackUrl);
            }

        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (IllegalStateException e) {
            return Response.status(501).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

    private void installApp(String uri, String appName, String fileName, String buildpackUrl) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            pushApplications(con, appName, fileName, buildpackUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushApplications(HttpsURLConnection con, String appName, String fileName, String buildpackUrl)
            throws IOException {
        try (InputStream in = con.getInputStream()) {

            String fileNameL = fileName.split("[^a-zA-Z0-9]")[0]; //untested

            String nameToUpload = Objects.equals(appName.toLowerCase(), fileNameL.toLowerCase()) ?
                    appName : fileNameL;

            String name = Inflector.getInstance().lowerCamelCase(nameToUpload);

            client.createApp(name, new Staging(null, buildpackUrl), 1000, 1000,
                    Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"));

            client.uploadApp(name, fileName, in, UploadStatusCallback.NONE);

            client.updateAppEnv(appName, ImmutableMap.of("appVersion", "1.0.0"));
        }
    }

    private String getLangBuildpack(String appLang) {
        switch (appLang) {
            case "java": return Buildpacks.JAVA.getUrl();
            case "python": return Buildpacks.PYTHON.getUrl();
            case "ruby": return Buildpacks.RUBY.getUrl();
            case "nodejs": return Buildpacks.NODEJS.getUrl();
            case "go": return Buildpacks.GO.getUrl();
            case "php": return Buildpacks.PHP.getUrl();
            case "hwc": return Buildpacks.HWC.getUrl();
            default: return "Unsupported language";
        }
    }
 }
