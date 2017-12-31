package org.elsys.rest;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> h = new HashSet<>();
        h.add( RestServiceOne.class );
        return h;
    }
}
