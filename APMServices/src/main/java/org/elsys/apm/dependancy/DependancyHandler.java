package org.elsys.apm.dependancy;

@FunctionalInterface
public interface DependancyHandler<T> {

    void handle(T t);
}
