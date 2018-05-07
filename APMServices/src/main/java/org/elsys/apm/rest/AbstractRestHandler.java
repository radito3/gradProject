package org.elsys.apm.rest;

import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.MessageFormat;

/**
 * Superclass to all REST handlers.
 * Contains the Json template for the responses.
 *
 * @author Rangel Ivanov
 */
public abstract class AbstractRestHandler {

    protected CloudClient client;

    protected final MessageFormat template = new MessageFormat("{\"error\":\"{0}\"," +
            "\"result\":\"{1}\",\"apps\":{2}}");

    /**
     * Creates the {@link CloudClient} object that the REST handlers use
     *
     * @param factory A {@link CloudClientFactory} object from which the Client is constructed
     * @param request The Json request
     * @param authType The authentication type
     */
    protected void createClient(CloudClientFactory factory, String request, String authType) {
        if (client == null) {
            JSONObject json = null;
            try {
                json = (JSONObject) new JSONParser().parse(request);
            } catch (ParseException ignored) {}

            assert json != null;

            if (authType.equals("token")) {
                client = factory.newCloudClient(json.get("token").toString());
            } else {
                client = factory.newCloudClient(json.get("user").toString(), json.get("pass").toString());
            }
        }
    }

    /**
     * Builds the Json for error messages
     *
     * @param message The error message
     * @return The Json
     */
    protected String errorMessage(String message) {
        return template.format(new Object[]{message, "", ""});
    }

    /**
     * Builds the Json for success messages
     *
     * @param message The success message
     * @return The Json
     */
    protected String successMessage(String message) {
        return template.format(new Object[]{"", message, ""});
    }

    //TODO Add JavaDoc documentation to all classes
}
