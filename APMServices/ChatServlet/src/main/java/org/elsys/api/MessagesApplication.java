package main.java.org.elsys.api;

import org.glassfish.jersey.server.ResourceConfig;

public class MessagesApplication extends ResourceConfig {

    public MessagesApplication() {
        packages("main.java.org.elsys.api.services");
    }

}
