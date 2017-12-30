package org.elsys.apm.dependancy;

import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DependencyHandlerImpl {

    private static List<Dependency> dependencies = new ArrayList<>();

    private static void handle(DependencyHandler handler) {
        dependencies.forEach(handler::handle);
    }

    static public void handle(JSONArray dependencies1, HttpsURLConnection con, String appName, String fileName,
                              String buildpackUrl, int memory, int disc) {
        JSONObject descr = Descriptor.getDescriptor();
        dependencies1.forEach(d -> dependencies.add(new Dependency(String.valueOf(d))));

        Class<?>[] parameters = {
                HttpsURLConnection.class, String.class, String.class, String.class, int.class, int.class
        };
        try {
            Method push = InstallApp.class.getDeclaredMethod("pushApps", parameters);
            handle(d -> {});
            push.invoke(null, con, appName, fileName, buildpackUrl, memory, disc);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
    }
}
