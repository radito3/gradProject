package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.Sets;
import org.elsys.apm.dependancy.*;
import org.elsys.apm.descriptor.Descriptor;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(ListApps.class, CloudClient.class, Buildpacks.class,
                InstallApp.class, DeleteApp.class, UpdateApp.class, Descriptor.class,
                Dependency.class, DependencyHandler.class, CloudClientFactory.class, CloudApp.class);
    }
}
