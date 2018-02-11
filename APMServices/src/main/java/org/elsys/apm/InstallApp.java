package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.apm.dependancy.DependencyHandler;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.parser.ParseException;

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

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.checkForApp(appName);

            CloudApp app = descr.getApp(appName);

            StringBuilder staticAppUrl = new StringBuilder(Descriptor.DESCRIPTOR_URL);
            staticAppUrl.replace(staticAppUrl.lastIndexOf("/") + 1, staticAppUrl.length(), app.getFileName());

            installApp(staticAppUrl.toString(), app, memory, disc);

            DependencyHandler.checkDependencies(app, client);

        } catch (ClassNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();

        } catch (IllegalArgumentException e) {
            return Response.status(415).entity(e.getMessage()).build();

        } catch (MissingResourceException e) {
            return Response.status(424).entity(e.getMessage()).build();

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(e.getMessage()).build();

        } finally {
            client.logout();
        }

        return Response.status(201).entity("App installed successfully").build();
    }

    public void installApp(String uri, CloudApp app, int memory, int disc)
            throws IOException, ClassNotFoundException, ParseException, IllegalArgumentException {

        URL url = new URL(uri);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        DependencyHandler.handle(app, this, memory, disc);

        pushApps(con, app, memory, disc);
    }

    private void checkLanguage(String buildpackUrl) {
        if (buildpackUrl.equals("Unsupported language")) {
            throw new IllegalArgumentException("Unsupported language");
        }
    }

    private void pushApps(HttpsURLConnection con, CloudApp app, int memory, int disc) throws IOException {
        try (InputStream in = con.getInputStream()) {

            String buildpackUrl = Buildpacks.getBuildpackUrl(app.getLanguage());
            checkLanguage(buildpackUrl);

            client.createApp(app.getName(), new Staging(null, buildpackUrl), disc, memory,
                    Collections.singletonList("https://cf-" + app.getName().toLowerCase() + ".cfapps.io"));

            client.uploadApp(app.getName(), app.getFileName(), in);

            client.updateAppEnv(app.getName(), ImmutableMap.of("pkgVersion", app.getVersion()));
        }
    }
 }
