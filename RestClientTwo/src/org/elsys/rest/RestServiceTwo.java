package org.elsys.rest;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Test")
public class RestServiceTwo {
	
	private static AtomicLong counter = new AtomicLong(0);
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getResponse() {
		return String.format("Called %d times", counter.getAndIncrement());
	}
}
