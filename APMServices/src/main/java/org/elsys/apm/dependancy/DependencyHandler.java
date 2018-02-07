package org.elsys.apm.dependancy;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.CloudApp;
import org.elsys.apm.CloudClient;
import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

public class DependencyHandler {

    private static ArrayList<Dependency> dependencies = new ArrayList<>();

    public static void handle(CloudApp app, InstallApp instance, int memory, int disc) {

        if (!app.hasDependencies()) return;

        app.getDependencies().forEach(obj -> dependencies.add(new Dependency(String.valueOf(obj))));

        try {
            Class<?>[] parameters = { String.class, CloudApp.class, int.class, int.class };
            Method push = InstallApp.class.getDeclaredMethod("installApp", parameters);

            dependencies.forEach(d -> {
                StringBuilder url = new StringBuilder(Descriptor.DESCRIPTOR_URL);
                url.replace(url.lastIndexOf("/") + 1, url.length(), d.getFileName());

                d.handle(push, instance, url.toString(), memory, disc);
            });

        } catch (NoSuchMethodException ignored) {}
    }
    
    public static void checkDependencies(CloudApp app, CloudClient client) throws MissingResourceException {

        JSONArray dependencies = app.getDependencies();

        List<String> dependenciesNames = (List<String>) dependencies.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        dependenciesNames.forEach(d -> {
            try {
                client.getApp(d);
            } catch (CloudFoundryException e) {
                throw new MissingResourceException("Missing dependencies", app.getName(), d);
            }
        });
    }
}
