package org.elsys.apm.dependancy;

import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DependencyHandler {

    private static ArrayList<Dependency> dependencies = new ArrayList<>();

    public static void handle(JSONArray dependencies1, int memory, int disc) {

        if (dependencies1.isEmpty()) return; // need to test it to make sure this works correctly

        Descriptor descr = Descriptor.getDescriptor();

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

                d.handle(push, url.toString(), appName, fileName, app, memory, disc);
            });
        } catch (NoSuchMethodException ignored) {}
    }
}
