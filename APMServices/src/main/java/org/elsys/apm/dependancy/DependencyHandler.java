package org.elsys.apm.dependancy;

@FunctionalInterface
public interface DependencyHandler {

    void handle(Dependency t);
}
