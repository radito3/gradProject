package org.elsys.apm.dependancy;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.CloudClient;
import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.MissingResourceException;

public class DependencyHandler {

    private static ArrayList<Dependency> dependencies = new ArrayList<>();

    private static Descriptor descr = Descriptor.getDescriptor();

    public static void handle(JSONArray dependencies1, InstallApp instance, int memory, int disc) {

        if (dependencies1.isEmpty()) return; // need to test it to make sure this works correctly

        dependencies1.forEach(obj -> dependencies.add(new Dependency(String.valueOf(obj))));

        Class<?>[] parameters = { String.class, String.class, String.class, JSONObject.class, int.class, int.class };
        try {
            Method push = InstallApp.class.getDeclaredMethod("installApp", parameters);

            dependencies.forEach(d -> {
                StringBuilder url = new StringBuilder(Descriptor.DESCRIPTOR_URL);
                url.replace(url.lastIndexOf("/") + 1, url.length(), d.getFileName());

                String appName = d.getName();
                String fileName = d.getFileName();
                JSONObject app = (JSONObject) descr.get(d.getName());

                d.handle(push, instance, url.toString(), appName, fileName, app, memory, disc);
            });
        } catch (NoSuchMethodException ignored) {}
    }
    
    public static void checkDependencies(String appName, CloudClient client) throws MissingResourceException {
        JSONObject app = (JSONObject) descr.get(appName);

        JSONArray dpnds = (JSONArray) app.get("dependencies");

        dpnds.forEach(d -> {
            try {
                client.getApp(appName);
            } catch (CloudFoundryException e) {
                throw new MissingResourceException("Missing dependencies", "", "");
            }
        });
    }
}
