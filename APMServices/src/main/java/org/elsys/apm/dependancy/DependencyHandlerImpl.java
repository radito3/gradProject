package org.elsys.apm.dependancy;

import org.elsys.apm.CloudClient;
import org.json.simple.JSONArray;

public class DependencyHandlerImpl {

    private static JSONArray dependencies;

    private static CloudClient client;

    private static void handle(DependencyHandler handler) {
        Dependency d = new Dependency("as", "sad");
        handler.handle(d);
    }

    static public void handle(CloudClient client1, JSONArray dependencies) {
        client = client1;
    }
}
