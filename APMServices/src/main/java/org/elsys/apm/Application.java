package org.elsys.apm;

import javax.ws.rs.ApplicationPath;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Arrays.stream(new Class<?>[]
                {ListApps.class, DeleteApp.class, InstallApp.class, UpdateApp.class})
                .collect(Collectors.toSet());
    }
}
