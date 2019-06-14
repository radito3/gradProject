package org.elsys.apm.rest;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.elsys.apm.ApplicationUploader;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.model.CloudApp;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Class for handling the REST calls for updating applications.
 *
 * @author Rangel Ivanov
 */
@Path("/{org}/{space}/update")
public class UpdateApp extends AbstractRestHandler {

    /**
     * Updates an application and returns the result of the operation
     *
     * @param authType The authentication type
     * @param appName The name of the application to be updated
     * @param orgName The organisation name
     * @param spaceName The space name
     * @param request The Json containing the authentication information
     * @return A Json containing the result of the operation
     */
    @PUT
    @Path("/{appName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUpdateResult(@HeaderParam("auth-type") String authType,
                                    @PathParam("appName") String appName,
                                    @PathParam("org") String orgName,
                                    @PathParam("space") String spaceName,
                                    String request) {
        CloudClientFactory factory = new CloudClientFactory(orgName, spaceName);
        createClient(factory, request, authType);

        client.login();

        try {
            CloudApplication cloudApp = client.getApp(appName);
            Descriptor descr = Descriptor.getDescriptor();
            descr.checkForApp(appName);

            CloudApp app = descr.getApp(appName);
            List<List<Integer>> versions = getVersions(cloudApp, app);

            ApplicationUploader uploader = new ApplicationUploader(client);
            uploader.checkDependencies(app);

            if (checkVer(versions.get(0), versions.get(1), 0)) {
                uploader.upload(client, (HttpsURLConnection) app.getFileUrl().openConnection(), app);
            } else {
                return Response.status(200).entity(successMessage("App up-to-date")).build();
            }

        } catch (CloudFoundryException e) {
            return Response.status(404).entity(errorMessage("App " + appName + " not found")).build();

        } catch (ClassNotFoundException e) {
            return Response.status(410).entity(errorMessage("App " + appName + " no longer supported")).build();

        } catch (MissingResourceException e) {
            return Response.status(424).entity(errorMessage(e.getMessage())).build();

        } catch (IOException | ParseException e) {
            return Response.status(500).entity(errorMessage(e.getMessage())).build();

        } finally {
            client.logout();
        }

        return Response.status(202).entity(successMessage("App updated")).build();
    }

    private boolean checkVer(List<Integer> currentVers, List<Integer> repoVers, int depth) {
        if (currentVers.get(depth) < repoVers.get(depth)) {
            return true;
        } else if (depth == 2) {
            return false;
        } else {
            return checkVer(currentVers, repoVers, depth + 1);
        }
    }

    private List<List<Integer>> getVersions(CloudApplication cloudApp, CloudApp app) {
        String repoVer = app.getVersion();
        String currentVer = cloudApp.getEnvAsMap().get("pkgVersion");

        List<Integer> repoVersions = new ArrayList<>(3);
        List<Integer> currentVersions = new ArrayList<>(3);

        Arrays.stream(currentVer.split("\\.")).forEach(ver -> currentVersions.add(Integer.parseInt(ver)));
        Arrays.stream(repoVer.split("\\.")).forEach(ver -> repoVersions.add(Integer.parseInt(ver)));

        return Arrays.asList(currentVersions, repoVersions);
    }
}
