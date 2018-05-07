package org.elsys.apm.rest;

import org.elsys.apm.CloudClient;

import java.text.MessageFormat;

public abstract class AbstractRestHandler {

    protected CloudClient client;

    protected final MessageFormat template = new MessageFormat("{\"error\":\"{0}\"," +
            "\"result\":\"{1}\",\"apps\":{2}}");

    protected void createClient() {


        template.format(new Object[]{"", "test-message", ""});
    }

    //TODO Add JavaDoc documentation to all classes
    //TODO Change the response type of all rest request handler from text_plain to json
    //TODO Implement usage of this abstract class which should be a superclass for all rest handlers
    //TODO The message template of this class must be used for consistency in all the rest responses
    //TODO The createClient() function should be used for the creation of the CloudClient to avoid redundancy
}
