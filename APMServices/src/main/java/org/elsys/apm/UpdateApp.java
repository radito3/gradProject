package org.elsys.apm;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.rest.CloudControllerClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/{org}/{space}/update/{appName}")
public class UpdateApp {

    @PathParam("org")
    private String orgName;

    @PathParam("space")
    private String spaceName;

    private CloudControllerClient client = new CloudControllerClientProvider(orgName, spaceName).getClient();
    private static final String buildpackUrl = "https://github.com/cloudfoundry/java-buildpack.git";

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String getUpdateResult(@PathParam("appName") String appName) {
        StringBuilder result = new StringBuilder();
        try {
            client.getApplication(appName);

            JSONObject descr = CloudControllerClientProvider
                    .getDescriptor(CloudControllerClientProvider.getStaticAppUrl() + "/descriptor.json");
            //check for newer version
            String ver = String.valueOf(descr.get("version"));
            Pattern pattern = Pattern.compile("^(\\d).(\\d).(\\d)$");
            Matcher matcher = pattern.matcher(ver);
            int Major = Integer.parseInt(matcher.group(1));
            int Minor = Integer.parseInt(matcher.group(2));
            int Build = Integer.parseInt(matcher.group(3));

            //if update ->
            result.append("App updated");
            //if up-to-date ->
            result.append("App already up-to-date");
        } catch (CloudFoundryException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                result.append(String.format("App %s not found", appName));
            } else {
                result.append(e.getMessage());
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        client.logout();
        return "UpdateApp Not Implemented";
    }
}
