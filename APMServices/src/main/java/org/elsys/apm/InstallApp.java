package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.CloudFoundryException;
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
import java.util.MissingResourceException;

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
        client = new CloudClientFactory(orgName, spaceName).newCloudClient(token);
        client.login();

        StringBuilder staticAppUrl = new StringBuilder(Descriptor.DESCRIPTOR_URL);
        try {
            //move the following few lines to Descriptor
            Descriptor descr = Descriptor.getDescriptor();
            JSONObject app = (JSONObject) descr.get(appName);
            if (app == null) {
                throw new ClassNotFoundException("App " + appName + " not found");
            }

            String fileName = String.valueOf(app.get("file"));
            staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), fileName);

            installApp(staticAppUrl.toString(), appName, fileName, app, memory, disc);

            DependencyHandler.checkDependencies(appName, client);
        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(e.getMessage()).build();
        } catch (MissingResourceException e) {
            return Response.status(424).entity(e.getMessage()).build();
        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

    public void installApp(String uri, String appName, String fileName, JSONObject app, int memory, int disc) {
        try {
            //you can convert this logic to handling objects with behavior, instead of handling json strings. 
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            String version = String.valueOf(app.get("pkgVersion"));
            String appLang = String.valueOf(app.get("language"));
            String buildpackUrl = getLangBuildpack(String.valueOf(appLang));
            if (buildpackUrl.equals("Unsupported language")) {
                throw new IllegalArgumentException("Unsupported language");
            }

            DependencyHandler.handle((JSONArray) app.get("dependencies"), this, memory, disc);

            pushApps(con, appName, fileName, buildpackUrl, version, memory, disc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//duplication ... and single responsibility
    private void pushApps(HttpsURLConnection con, String appName, String fileName,
                         String buildpackUrl, String version, int memory, int disc) {
        try (InputStream in = con.getInputStream()) {

            client.createApp(appName, new Staging(null, buildpackUrl), disc, memory,
                    Collections.singletonList("https://cf-" + appName.toLowerCase() + ".cfapps.io"));

            client.uploadApp(appName, fileName, in);

            client.updateAppEnv(appName, ImmutableMap.of("pkgVersion", version));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloudFoundryException e) {
            System.err.println(e.getDescription());
        }
    }
    //this method could be included in the Buildpacks enum
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
            case "binary": return Buildpacks.BINARY.getUrl();
            default: return "Unsupported language";
        }
    }
 }
