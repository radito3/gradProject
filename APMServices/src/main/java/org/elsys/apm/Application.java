package org.elsys.apm;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        // You know this would be a bit faster ;)
        // return jersey.repackaged.com.google.common.collect.Sets.newHashSet(ListApps.class, CloudControllerClientProvider.class,
        // Buildpacks.class, InstallApp.class,
        // DeleteApp.class, UpdateApp.class, ThreadHolder.class);
        return Arrays.stream(new Class<?>[]
                {ListApps.class, CloudControllerClientProvider.class, Buildpacks.class, InstallApp.class,
                DeleteApp.class, UpdateApp.class, ThreadHolder.class})
                .collect(Collectors.toSet());
    }
}
