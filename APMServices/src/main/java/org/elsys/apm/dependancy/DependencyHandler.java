package org.elsys.apm.dependancy;

import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DependencyHandler {

    private static List<Dependency> dependencies = new ArrayList<>();

    static public void handle(JSONArray dependencies1, int memory, int disc) {

        if (dependencies1.isEmpty()) return; // need to test it to make sure this works correctly

        Descriptor descr = Descriptor.getDescriptor();

        dependencies1.forEach(d -> dependencies.add(new Dependency(String.valueOf(d))));

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
