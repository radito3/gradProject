package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.apm.dependancy.DependencyHandler;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

@Path("/{org}/{space}/install/{appName}")
public class InstallApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudClient client;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInstallResult(@HeaderParam("access-token") String token,
                                     @PathParam("appName") String appName,
                                     @DefaultValue("1000") @QueryParam("mem") int memory,
                                     @DefaultValue("1000") @QueryParam("disc") int disc) {
        client = new CloudClient(orgName, spaceName, token);
        client.login();

        StringBuilder staticAppUrl = new StringBuilder(Descriptor.DESCRIPTOR_URL);
        try {
            JSONObject descr = Descriptor.getDescriptor();
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            String fileName = String.valueOf(app.get("file"));
            staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), fileName);

            installApp(staticAppUrl.toString(), appName, fileName, app, memory, disc);
        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

    public void installApp(String uri, String appName, String fileName, JSONObject app, int memory, int disc) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            String version = String.valueOf(app.get("pkgVersion"));
            String appLang = String.valueOf(app.get("language"));
            String buildpackUrl = getLangBuildpack(String.valueOf(appLang));
            if (buildpackUrl.equals("Unsupported language")) {
                throw new IllegalArgumentException("Unsupported language");
            }

            DependencyHandler.handle((JSONArray) app.get("dependencies"), memory, disc);

            pushApps(con, appName, fileName, buildpackUrl, version, memory, disc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushApps(HttpsURLConnection con, String appName, String fileName,
                         String buildpackUrl, String version, int memory, int disc) {
        try (InputStream in = con.getInputStream()) {
            String name = fileName.split("[^-a-zA-Z0-9]")[0].toLowerCase();

            client.createApp(name, new Staging(null, buildpackUrl), disc, memory,
                    Collections.singletonList("https://" + appName.toLowerCase() + ".cfapps.io"));

            client.uploadApp(name, fileName, in, UploadStatusCallback.NONE);

            client.updateAppEnv(appName, ImmutableMap.of("pkgVersion", version));
        } catch (IOException e) {
            e.printStackTrace();
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
            case "dotnet": return Buildpacks.DOTNET.getUrl();
            default: return "Unsupported language";
        }
    }
 }
