package org.elsys.apm.dependancy;

import org.elsys.apm.CloudApp;
import org.elsys.apm.InstallApp;
import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Dependency {

    private CloudApp app;

    public Dependency(String appName) {
        //need to check for existing app!!!
        try {
            this.app = Descriptor.getDescriptor().getApp(appName);
        } catch (IOException | ParseException e) {
            e.printStackTrace(); //need to handle this
        }
    }

    public String getFileName() {
        return app.getFileName();
    }

    public void handle(Method method, InstallApp instance, String url, int memory, int disc) {
        try {
            method.invoke(instance, url, app, memory, disc);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }
}
