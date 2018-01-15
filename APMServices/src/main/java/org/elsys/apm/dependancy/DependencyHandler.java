package org.elsys.apm.dependancy;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.CloudClient;
import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

public class DependencyHandler {

    private static ArrayList<Dependency> dependencies = new ArrayList<>();

    private static Descriptor descr = Descriptor.getDescriptor();

    public static void handle(JSONArray dependencies1, InstallApp instance, int memory, int disc) {

        if (dependencies1.isEmpty()) return;

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

        JSONArray dependencies = (JSONArray) app.get("dependencies");

        List<String> dependenciesNames = (List<String>) dependencies.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        dependenciesNames.forEach(d -> {
            try {
                client.getApp(d);
            } catch (CloudFoundryException e) {
                throw new MissingResourceException("Missing dependencies", appName, d);
            }
        });
    }
}
