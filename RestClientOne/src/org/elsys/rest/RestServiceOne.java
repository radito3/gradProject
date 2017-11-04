package org.elsys.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Test")
public class RestServiceOne {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getResponse() {
		StringBuilder result = new StringBuilder();
		result.append("Service Two response: ");

		try {
			String uri = System.getenv("restServiceTwoUri");
			URL obj = new URL(uri);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

	 		con.setRequestMethod("GET");
	 		
	 		InputStream in = con.getInputStream();

	 		result.append(new BufferedReader(new InputStreamReader(in))
	 				  .lines().collect(Collectors.joining("\n")));
	 		
	 		in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}
