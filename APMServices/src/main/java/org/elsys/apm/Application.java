package org.elsys.apm;

import jersey.repackaged.com.google.common.collect.Sets;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(ListApps.class, CloudClient.class, Buildpacks.class,
                InstallApp.class, DeleteApp.class, UpdateApp.class, DescriptorWork.class);
    }
}
