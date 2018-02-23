package org.elsys.apm.model;

import org.elsys.apm.repository.RepositoryURLBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class CloudApp {

    private String name;

    private String language;

    private String version;

    private String fileName;

    private JSONArray dependencies;

    public CloudApp(JSONObject app, String appName) {
        this.name = appName;
        this.language = String.valueOf(app.get("language"));
        this.dependencies = (JSONArray) app.get("dependencies");
        this.fileName = String.valueOf(app.get("file"));
        this.version = String.valueOf(app.get("pkgVersion"));
    }

    public String getVersion() {
        return version;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    public JSONArray getDependencies() {
        return dependencies;
    }

    public URL getFileUrl() throws MalformedURLException {

        RepositoryURLBuilder urlBuilder = new RepositoryURLBuilder();

        return urlBuilder.repoRoot().target(fileName).build();
    }
}
