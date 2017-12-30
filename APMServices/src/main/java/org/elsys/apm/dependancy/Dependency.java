package org.elsys.apm.dependancy;

import org.elsys.apm.descriptor.Descriptor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Dependency {

    private String name;
    private String fileName;
    private String version;
    private List<Dependency> dependencies;
    private JSONObject descriptor = Descriptor.getDescriptor();

    public Dependency(String appName) {
        this.name = appName;
        JSONObject app = (JSONObject) descriptor.get(appName);
        this.fileName = String.valueOf(app.get("file"));
        this.version = String.valueOf(app.get("pkgVersion"));
        this.dependencies = new LinkedList<>();

        JSONArray pkgs = (JSONArray) app.get("dependencies");
        if (!pkgs.isEmpty()) {
            pkgs.forEach(pkg -> this.dependencies.add(new Dependency(String.valueOf(pkg))));
        }
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getVersion() {
        return version;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean hasDependencies() {
        return dependencies.isEmpty();
    }
}
