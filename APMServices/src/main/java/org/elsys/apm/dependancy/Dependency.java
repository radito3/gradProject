package org.elsys.apm.dependancy;

import java.util.LinkedList;
import java.util.List;

public class Dependency {

    private String name;
    private String fileName;
    private List<Dependency> dependencies;

    public Dependency(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.dependencies = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean hasDependencies() {
        return dependencies.isEmpty();
    }

    public void addDependency(Dependency d) {
        dependencies.add(d);
    }
}
