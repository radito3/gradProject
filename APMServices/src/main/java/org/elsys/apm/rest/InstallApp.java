package org.elsys.apm.rest;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.cloudfoundry.client.lib.domain.Staging;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.dependency.DependencyHandler;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.Buildpacks;
import org.elsys.apm.model.CloudApp;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
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
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        client = factory.newCloudClient(token);

        client.login();

        try {
            Descriptor descr = Descriptor.getDescriptor();
            descr.checkForApp(appName);

            CloudApp app = descr.getApp(appName);

            RepositoryURLBuilder urlBuilder = new RepositoryURLBuilder();
            URL fileUrl = urlBuilder.repoRoot().target(app.getFileName()).build();

            installApp(fileUrl, app, memory, disc);

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

    public void installApp(URL url, CloudApp app, int memory, int disc)
            throws IOException, ClassNotFoundException, ParseException, IllegalArgumentException {

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
