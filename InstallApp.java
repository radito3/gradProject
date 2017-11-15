package org.elsys.<something>;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;

@Path("/Test")
public class FileTransfer {

	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFiles() {
		DefaultConnectionContext d = DefaultConnectionContext.builder()
            .apiHost(System.getenv("uri"))
            .build();
    
        PasswordGrantTokenProvider p = PasswordGrantTokenProvider.builder()
            .password(System.getenv("pass"))
            .username(System.getenv("user"))
            .build();
        
     
        ReactorCloudFoundryClient r = ReactorCloudFoundryClient.builder()
	        .connectionContext(d)
	        .tokenProvider(p)
	        .build();
        
        DefaultCloudFoundryOperations.builder()
	        .cloudFoundryClient(r)
	        .organization("graduationProject")
	        .space("development")
	        .build();
       
	        
		return "success";
    }

}
