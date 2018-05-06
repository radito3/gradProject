package org.elsys.apm.model;

import jersey.repackaged.com.google.common.collect.Sets;
import org.elsys.apm.ApplicationUploader;
import org.elsys.apm.CloudClient;
import org.elsys.apm.CloudClientFactory;
import org.elsys.apm.CloudClientImpl;
import org.elsys.apm.Installable;
import org.elsys.apm.descriptor.Descriptor;
import org.elsys.apm.repository.RepositoryURLBuilder;
import org.elsys.apm.rest.DeleteApp;
import org.elsys.apm.rest.InstallApp;
import org.elsys.apm.rest.ListApps;
import org.elsys.apm.rest.UpdateApp;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

/**
 * Sets the base path for the Application.
 * Configures the classes used in the Application.
 */
@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(ListApps.class, CloudClientImpl.class, Buildpacks.class,
                InstallApp.class, DeleteApp.class, UpdateApp.class, Descriptor.class,
                ApplicationUploader.class, Installable.class, CloudClientFactory.class,
                CloudApp.class, RepositoryURLBuilder.class, CloudClient.class);
    }
}
