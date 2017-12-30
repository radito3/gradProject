package org.elsys.apm.dependancy;

import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Dependency {

    private String name;
    private String fileName;

    public Dependency(String appName) {
        this.name = appName;
        JSONObject app = (JSONObject) Descriptor.getDescriptor().get(appName);
        this.fileName = String.valueOf(app.get("file"));
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public void handle(Method method, Object... args) {
        try {
            method.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }
}
