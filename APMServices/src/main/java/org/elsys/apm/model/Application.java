package org.elsys.apm.model;

import jersey.repackaged.com.google.common.collect.Sets;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientImpl;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.dependency.Dependency;
import org.elsys.apm.dependency.DependencyHandler;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.elsys.apm.rest.DeleteApp;
import org.elsys.apm.rest.InstallApp;
import org.elsys.apm.rest.ListApps;
import org.elsys.apm.rest.UpdateApp;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(ListApps.class, CloudClientImpl.class, Buildpacks.class,
                InstallApp.class, DeleteApp.class, UpdateApp.class, Descriptor.class,
                Dependency.class, DependencyHandler.class, CloudClientFactory.class,
                CloudApp.class, RepositoryURLBuilder.class, CloudClient.class);
    }
}
