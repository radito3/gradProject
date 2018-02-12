package org.elsys.apm.dependency;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.elsys.apm.model.CloudApp;
import org.elsys.apm.CloudClient;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.elsys.apm.rest.InstallApp;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

public class DependencyHandler {

    private static ArrayList<Dependency> dependencies = new ArrayList<>();

    public static void handle(CloudApp app, InstallApp instance, int memory, int disc)
            throws IOException, ParseException, ClassNotFoundException {

        if (!app.hasDependencies()) return;

        for (Object o : app.getDependencies()) {
            dependencies.add(new Dependency(String.valueOf(o)));
        }

        try {
            Class<?>[] parameters = { URL.class, CloudApp.class, int.class, int.class };
            Method push = InstallApp.class.getDeclaredMethod("installApp", parameters);

            for (Dependency d : dependencies) {
                RepositoryURLBuilder url = new RepositoryURLBuilder();

                d.handle(push, instance, url.repoRoot().target(d.getFileName()).build(), memory, disc);
            }

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
